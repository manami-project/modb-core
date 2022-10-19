package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.test.tempDirectory
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS.MAC
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.time.Duration

internal class StringExtensionsKtTest {

    @Nested
    inner class WriteToFileTests {

        @Test
        fun `throws exception if given Path already exists, but is a directory`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test").createDirectory()

                // when
                val result = assertThrows<Exception> { //exception type varies depending on the OS
                    "text".writeToFile(file, false)
                }

                // then
                assertThat(result).hasMessageContaining(file.toString()) //message varies depending on the OS
            }
        }

        @Test
        fun `throws exception if the string is empty`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt")

                // when
                val result = assertThrows<IllegalStateException> {
                    EMPTY.writeToFile(file, false)
                }

                // then
                assertThat(result).hasMessage("Trying to write file [$file], but string was blank")
            }
        }

        @Test
        fun `throws exception if the string is blank`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt")

                // when
                val result = assertThrows<IllegalStateException> {
                    "    ".writeToFile(file, false)
                }

                // then
                assertThat(result).hasMessage("Trying to write file [$file], but string was blank")
            }
        }

        @Test
        fun `successfully write string without lock file`() {
            tempDirectory {
                // given
                val string = "Some content\nfor a test file."
                val file = tempDir.resolve("test.txt")

                // when
                string.writeToFile(file, false)

                // then
                assertThat(file).exists()
                assertThat(runBlocking { file.readFileSuspendable() }).isEqualTo(string)
            }
        }

        @Test
        fun `overrides file if the file already exists`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt").createFile()
                "Some content\nfor a test file.".writeToFile(file)

                val string = "Some totally different content."

                // when
                string.writeToFile(file, false)

                // then
                assertThat(file).exists()
                assertThat(runBlocking { file.readFileSuspendable() }).isEqualTo(string)
            }
        }

        @Test
        @Timeout(value = 1, unit = MINUTES)
        @DisabledOnOs(MAC, disabledReason = "The FS watch service of macos takes way too long to register that there is a new file.")
        fun `successfully write string using lock file`() {
            tempDirectory {
                // given
                val watchService = FileSystems.getDefault().newWatchService()
                tempDir.register(watchService, ENTRY_CREATE)

                val string = "Some content\nfor a test file."
                val file = tempDir.resolve("test.txt")
                var isLockFileCreated = false

                runBlocking {
                    val watchServiceJob = launch {
                        withTimeout(Duration.parse("1m")) {
                            var key = watchService.takeOrNullSuspendable()

                            while (key != null && isActive && !isLockFileCreated) {
                                val events = key.pollEvents()
                                events.find { it.kind() == ENTRY_CREATE && (it.context() as Path).fileName.toString().endsWith(LOCK_FILE_SUFFIX)}?.let { isLockFileCreated = true }

                                key.reset()
                                key = watchService.takeOrNullSuspendable()
                            }
                        }
                    }


                    // when
                    string.writeToFile(file, true)
                    watchServiceJob.join()
                }

                // then
                assertThat(file).exists()
                assertThat(runBlocking { file.readFileSuspendable() }).isEqualTo(string)
                assertThat(isLockFileCreated).isTrue()
                assertThat(file.changeSuffix("lck")).doesNotExist()
            }
        }
    }

    @Nested
    inner class RemoveTests {

        @Test
        fun `doesn't remove anything, because the string doesn't contain the value`() {
            // given
            val initial = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt
                ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores
                et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.""".trimIndent()

            // when
            val result = initial.remove("test")

            // then
            assertThat(result).isEqualTo(initial)
        }

        @Test
        fun `doesn't remove value if ignoreCase is false and case doesn't fit`() {
            // given
            val initial = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet.""".trimIndent()

            // when
            val result = initial.remove("stet")

            // then
            assertThat(result).isEqualTo(initial)
        }

        @Test
        fun `removes single occurrence if ignoreCase is false and case fits - doesn't normalize whitespaces`() {
            // given
            val initial = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet.""".trimIndent()

            // when
            val result = initial.remove("Stet")

            // then
            assertThat(result).isEqualTo("""Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum.  clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet.""".trimIndent())
        }

        @Test
        fun `removes single occurrence if ignoreCase is true and case doesn't fit - doesn't normalize whitespaces`() {
            // given
            val initial = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet.""".trimIndent()

            // when
            val result = initial.remove("stet", ignoreCase = true)

            // then
            assertThat(result).isEqualTo("""Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum.  clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet.""".trimIndent())
        }

        @Test
        fun `removes multiple occurrences if ignoreCase is false and case fits - doesn't normalize whitespaces`() {
            // given
            val initial = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
                amet.""".trimIndent()

            // when
            val result = initial.remove("sit")

            // then
            assertThat(result).isEqualTo("""Lorem ipsum dolor  amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor 
                amet.""".trimIndent())
        }

        @Test
        fun `removes multiple occurrences if ignoreCase is true and case varies - doesn't normalize whitespaces`() {
            // given
            val initial = """Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor SIT
                amet.""".trimIndent()

            // when
            val result = initial.remove("sit", ignoreCase = true)

            // then
            assertThat(result).isEqualTo("""Lorem ipsum dolor  amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor
                invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo
                duo dolores et ea rebum. Stet clita kasd test gubergren, no sea takimata sanctus est Lorem ipsum dolor 
                amet.""".trimIndent())
        }
    }

    @Nested
    inner class NormalizeWhitespacesTests {

        @ParameterizedTest
        @ValueSource(strings = ["a b", "a  b", "a   b", "a    b"])
        fun `replaces multiple consecutive whitespaces with a single one`(value: String) {
            // when
            val result = value.normalizeWhitespaces()

            // then
            assertThat(result).isEqualTo("a b")
        }

        @ParameterizedTest
        @ValueSource(strings = ["a b", " a b", "a b ", " ab ", "ab"])
        fun `does nothing if there are no multiple consecutive whitespaces`(value: String) {
            // when
            val result = value.normalizeWhitespaces()

            // then
            assertThat(result).isEqualTo(value)
        }
    }
}