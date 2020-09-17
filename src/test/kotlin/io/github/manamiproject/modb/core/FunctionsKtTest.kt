package io.github.manamiproject.modb.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        fun `throws an exception if the the given path is a directory`() {
            val path = "load_resource_tests"

            // when
            val result = assertThrows<IllegalStateException> {
                loadResource(path)
            }

            // then
            assertThat(result).hasMessage("Given path [$path] is not a file.")
        }

        @Test
        fun `throws an exception if the the given path does not exist`() {
            val path = "non-existent-file.txt"

            // when
            val result = assertThrows<IllegalStateException> {
                loadResource(path)
            }

            // then
            assertThat(result).hasMessage("Given path [$path] does not exist")
        }
    }
}