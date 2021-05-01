package io.github.manamiproject.modb.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class FunctionsKtTest {

    @Nested
    inner class LoadResourceTests {

        @Test
        fun `load test resource from root directory`() {
            // when
            val result = loadResource("load_resource_tests/test-file.txt")

            // then
            assertThat(result).isEqualTo("File in\n\nroot directory.")
        }

        @Test
        fun `load test resource from subdirectory`() {
            // when
            val result = loadResource("load_resource_tests/subdirectory/other-test-file.txt")

            // then
            assertThat(result).isEqualTo("File in\nsubdirectory.")
        }

        @Test
        fun `returns a list of the names of the elements if the given path is a directory`() {
            val path = "load_resource_tests"

            // when
            val result = loadResource(path)

            // then
            assertThat(result).isEqualTo("subdirectory\ntest-file.txt\n")
        }

        @Test
        fun `throws an exception if the the given path does not exist`() {
            val path = "non-existent-file.txt"

            // when
            val result = assertThrows<IllegalStateException> {
                loadResource(path)
            }

            // then
            assertThat(result).hasMessage("Unable to load file [$path]")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "   "])
        fun `throws an exception if the the given path is blank or empty`(value: String) {
            // when
            val result = assertThrows<IllegalArgumentException> {
                loadResource(value)
            }

            // then
            assertThat(result).hasMessage("Given path must not be blank")
        }
    }

    @Nested
    inner class ResourceFileExists {

        @Test
        fun `returns true if the file exists`() {
            // given
            val path = "resource_file_exists_tests/test-file.txt"

            // when
            val result = resourceFileExists(path)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns false if the file exists`() {
            // given
            val path = "resource_file_exists_tests/non-existent-file.txt"

            // when
            val result = resourceFileExists(path)

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `returns false if the given path is a directory`() {
            // given
            val path = "resource_file_exists_tests"

            // when
            val result = resourceFileExists(path)

            // then
            assertThat(result).isFalse()
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "   "])
        fun `throws an exception if the the given path is blank or empty`(value: String) {
            // when
            val result = assertThrows<IllegalArgumentException> {
                resourceFileExists(value)
            }

            // then
            assertThat(result).hasMessage("Given path must not be blank")
        }
    }
}