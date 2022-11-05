package io.github.manamiproject.modb.core.coroutines

import io.github.manamiproject.modb.core.coroutines.CoroutineManager.runCoroutine
import kotlinx.coroutines.delay
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

internal class CoroutineManagerTest {

    @Nested
    inner class RunCoroutineTests {

        @Test
        fun `successfully execute action`() {
            // given
            val testFunction: suspend () -> String = {
                delay(1)
                "successful"
            }

            // when
            val result = runCoroutine {
                testFunction()
            }

            // then
            assertThat(result).isEqualTo("successful")
        }

        @Test
        fun `throws an exception up to parent`() {
            // given
            val testFunction: suspend () -> String = {
                delay(1)
                throw IllegalStateException("test")
            }

            // when
            val result = assertThrows<IllegalStateException> {
                runCoroutine {
                    testFunction()
                }
            }

            // then
            assertThat(result).hasMessage("test")
        }
    }
}