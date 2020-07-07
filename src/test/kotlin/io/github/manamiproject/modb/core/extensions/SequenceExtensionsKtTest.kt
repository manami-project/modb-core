package io.github.manamiproject.modb.core.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SequenceExtensionsKtTest {

    @Nested
    inner class DoIfNotEmptyTests {

        @Test
        fun `invoke lambda if given iterable is not empty`() {
            // given
            var hasBeenInvoked = false

            // when
            listOf("a", "B").asSequence().map { it.toLowerCase() }.doIfNotEmpty { hasBeenInvoked = true }

            // then
            assertThat(hasBeenInvoked).isTrue()
        }

        @Test
        fun `don't invoke lambda if given iterable empty`() {
            // given
            var hasBeenInvoked = false

            // when
            listOf(1, 2).asSequence().filterNot { it < 3 }.doIfNotEmpty { hasBeenInvoked = true }

            // then
            assertThat(hasBeenInvoked).isFalse()
        }
    }
}