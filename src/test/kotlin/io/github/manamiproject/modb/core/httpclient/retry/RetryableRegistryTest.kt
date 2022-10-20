package io.github.manamiproject.modb.core.httpclient.retry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RetryableRegistryTest {

    @AfterEach
    fun afterEach() {
        RetryableRegistry.clear()
    }

    @Nested
    inner class RegisterTests {

        @Test
        fun `returns true if a new retry was registered successfully`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior()

            // when
            val result = RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // then
            assertThat(result).isTrue()
            assertThat(RetryableRegistry.fetch(testRetryBehaviorName)).isNotNull()
        }

        @Test
        fun `returns false if the retry hasn't been added, because a retry with the same name has already been registered`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior()

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // when
            val result = RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class DeregisterTests {

        @Test
        fun `returns true if an existing retry was registered successfully`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior()

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // when
            val result = RetryableRegistry.deregister(testRetryBehaviorName)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns false if a retry with the given name doesn't exist`() {
            // when
            val result = RetryableRegistry.deregister("any name")

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class FetchTests {

        @Test
        fun `returns the retry with the given name`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior()

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // when
            val result = RetryableRegistry.fetch(testRetryBehaviorName)

            // then
            assertThat(result).isNotNull()
        }

        @Test
        fun `returns null if a retry with the given name doesn't exist`() {
            // when
            val result = RetryableRegistry.fetch("any name")

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class ClearTests {

        @Test
        fun `successfully removed all entries`() {
            // given
            val retryBehavior = RetryBehavior()

            RetryableRegistry.register("1", retryBehavior)
            RetryableRegistry.register("2", retryBehavior)
            RetryableRegistry.register("3", retryBehavior)
            RetryableRegistry.register("4", retryBehavior)

            // when
            RetryableRegistry.clear()

            // then
            assertThat(RetryableRegistry.fetch("1")).isNull()
            assertThat(RetryableRegistry.fetch("2")).isNull()
            assertThat(RetryableRegistry.fetch("3")).isNull()
            assertThat(RetryableRegistry.fetch("4")).isNull()
        }
    }
}