package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

internal class FunctionsKtTest {

    @Nested
    inner class LoadResourceTests {

        @Test
        fun `load test resource from root directory`() {
            runBlocking {
                // when
                val result = loadResource("load_resource_tests/test-file.txt")

                // then
                assertThat(result).isEqualTo("File in\n\nroot directory.")
            }
        }

        @Test
        fun `load test resource from subdirectory`() {
            runBlocking {
                // when
                val result = loadResource("load_resource_tests/subdirectory/other-test-file.txt")

                // then
                assertThat(result).isEqualTo("File in\nsubdirectory.")
            }
        }

        @Test
        fun `returns a list of the names of the elements if the given path is a directory`() {
            runBlocking {
                val path = "load_resource_tests"

                // when
                val result = loadResource(path)

                // then
                assertThat(result).isEqualTo("subdirectory\ntest-file.txt\n")
            }
        }

        @Test
        fun `throws an exception if the the given path does not exist`() {
            val path = "non-existent-file.txt"

            // when
            val result = exceptionExpected<IllegalStateException> {
                loadResource(path)
            }

            // then
            assertThat(result).hasMessage("Unable to load file [$path]")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", "   "])
        fun `throws an exception if the the given path is blank or empty`(value: String) {
            // when
            val result = exceptionExpected<IllegalArgumentException> {
                loadResource(value)
            }

            // then
            assertThat(result).hasMessage("Given path must not be blank.")
        }
    }

    @Nested
    inner class ResourceFileExistsTests {

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
            assertThat(result).hasMessage("Given path must not be blank.")
        }
    }

    @Nested
    inner class RandomTests {

        @Test
        fun `parameter cannot be equal`() {
            // when
            val result = assertThrows<IllegalArgumentException> {
                random(4, 4)
            }

            // then
            assertThat(result).hasMessage("Numbers cannot be equal.")
        }

        @Test
        fun `generates a random number within the given interval`() {
            // given
            val min = 1
            val max = 2

            // when
            val result = random(min, max)

            // then
            assertThat(result).isBetween(min.toLong(), max.toLong())
        }

        @Test
        fun `order of the parameters doesn't matter`() {
            // given
            val min = 1
            val max = 2

            // when
            val result = random(max, min)

            // then
            assertThat(result).isBetween(min.toLong(), max.toLong())
        }
    }

    @Nested
    inner class ExcludeFromTestContextTests {

        @Test
        fun `execute code if current context is not test context`() {
            runBlocking {
                // given
                var hasBeenInvoked = false

                val testConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun isTestContext(): Boolean = false
                }

                // when
                excludeFromTestContext(testConfig) { hasBeenInvoked = true }

                // then
                assertThat(hasBeenInvoked).isTrue()
            }
        }

        @Test
        fun `don't execute code if current context is test context`() {
            runBlocking {
                // given
                var hasBeenInvoked = false

                val testConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun isTestContext(): Boolean = true
                }

                // when
                excludeFromTestContext(testConfig) { hasBeenInvoked = true }

                // then
                assertThat(hasBeenInvoked).isFalse()
            }
        }
    }
}