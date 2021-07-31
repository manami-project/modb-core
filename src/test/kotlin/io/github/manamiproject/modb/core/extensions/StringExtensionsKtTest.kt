package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS.MAC
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.lang.Thread.sleep
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE

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
                assertThat(file.readFile()).isEqualTo(string)
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
                assertThat(file.readFile()).isEqualTo(string)
            }
        }

        @Test
        @DisabledOnOs(MAC)
        fun `successfully write string using lock file`() {
            tempDirectory {
                // given
                val watchService = FileSystems.getDefault().newWatchService()
                tempDir.register(watchService, ENTRY_CREATE)

                var isLockFileCreated = false

                Thread {
                    var key = watchService.takeOrNull()

                    while (key != null) {
                        val events = key.pollEvents()
                        events.find { it.kind() == ENTRY_CREATE && (it.context() as Path).fileName.toString().endsWith(LOCK_FILE_SUFFIX)}?.let { isLockFileCreated = true }

                        key.reset()
                        key = watchService.takeOrNull()
                    }
                }.start()

                val string = "Some content\nfor a test file."
                val file = tempDir.resolve("test.txt")

                sleep(4000)

                // when
                string.writeToFile(file, true)

                // then
                sleep(4000)
                assertThat(file).exists()
                assertThat(file.readFile()).isEqualTo(string)
                assertThat(isLockFileCreated).isTrue()
                assertThat(file.changeSuffix("lck")).doesNotExist()
            }
        }
    }

    @Nested
    inner class IsIntTests {

        @ParameterizedTest
        @ValueSource(strings = ["0", "1", "10", "100", "1000", "43252446346"])
        fun `return true, because the the given value represents an Int`(value: String) {
            // when
            val result = value.isInt()

            // then
            assertThat(result).isTrue()
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "  ", " 4", "5 ", "123,3", "123.5", "2021-07-31"])
        fun `returns false, because the given value is not a representation of an Int`(value: String) {
            // when
            val result = value.isInt()

            // then
            assertThat(result).isFalse()
        }
    }
}