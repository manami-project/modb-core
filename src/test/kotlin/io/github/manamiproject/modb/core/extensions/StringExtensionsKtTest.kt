package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.test.Test

internal class StringExtensionsKtTest {

    @Nested
    inner class WriteToFileTests {

        @Test
        fun `throws exception if given Path already exists, but is a directory`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test").createDirectory()

                // when
                val result = exceptionExpected<Exception> { //exception type varies depending on the OS
                    "text".writeToFile(file, false)
                }

                // then
                assertThat(result).hasMessageContaining(file.toString()) //message varies depending on the OS
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "    "])
        fun `throws exception if the string is empty`(value: String) {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt")

                // when
                val result = exceptionExpected<IllegalStateException> {
                    value.writeToFile(file, false)
                }

                // then
                assertThat(result).hasMessage("Tried to write file [$file], but the String was blank.")
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
        @ValueSource(strings = ["\u00A0", "\u202F", "\uFEFF", "\u2007", "\u180E", "\u2060", "\u200D"])
        fun `correctly normalizes whitespaces`(value: String) {
            // when
            val result = "  a${value}bc${value}${value}de${value}${value}${value}f ".normalizeWhitespaces()

            // then
            assertThat(result).isEqualTo("a bc de f")
        }

        fun `correctly normalizes zero width non joiner`() {
            // when
            val result = "Ba\u200Cek".normalizeWhitespaces()

            // then
            assertThat(result).isEqualTo("Baek")
        }
    }

    @Nested
    inner class NormalizeTests {

        @Test
        fun `correctly normalize strings`() {
            // when
            val result = "  \u00A0 a\u200Ce \u202F b \uFEFF c \u2007 d \u180E e \u2060 f \u200D g \u200C \r h \r\r i \r\n j \r\n\r\n k \n l \n\n m \t n \t\t o  ".normalize()

            // then
            assertThat(result).isEqualTo("ae b c d e f g h i j k l m n o")
        }
    }
}