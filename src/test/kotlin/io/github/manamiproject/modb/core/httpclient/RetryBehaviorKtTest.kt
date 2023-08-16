package io.github.manamiproject.modb.core.httpclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test

internal class RetryBehaviorKtTest {

    @Nested
    inner class AddCaseTests {

        @Test
        fun `adding duplicates is possible`() {
            // given
            val retryBehavior = RetryBehavior().apply {
                addCase(
                    retryIf = { httpResponse -> httpResponse.code == 403 },
                    executeBeforeRetry = { println("this statement has bee executed before the retry") }
                )
            }

            // when
            retryBehavior.addCase(
                retryIf = { httpResponse -> httpResponse.code == 403 },
                executeBeforeRetry = { println("this statement has bee executed before the retry") }
            )

            // then
            assertThat(retryBehavior.cases.size).isEqualTo(2)
        }

        @Test
        fun `returns true when adding a new case`() {
            // given
            val retryBehavior = RetryBehavior()

            // when
            val result = retryBehavior.addCase(
                retryIf = { httpResponse: HttpResponse -> httpResponse.code == 403 },
                executeBeforeRetry = { }
            )

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns false if case wasn't added, because a matching entry already exists`() {
            // given
            val retryIf = { httpResponse: HttpResponse -> httpResponse.code == 403 }
            val retryBehavior = RetryBehavior().apply {
                addCase(
                    retryIf = retryIf,
                    executeBeforeRetry = { println("this statement has bee executed before the retry") }
                )
            }

            // when
            val result = retryBehavior.addCase(
                retryIf = retryIf,
                executeBeforeRetry = { }
            )

            // then
            assertThat(result).isFalse()
        }
    }
}