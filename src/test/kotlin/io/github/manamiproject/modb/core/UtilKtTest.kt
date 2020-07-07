package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UtilKtTest {

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

        @Test
        fun `don't execute code if current context is test context`() {
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