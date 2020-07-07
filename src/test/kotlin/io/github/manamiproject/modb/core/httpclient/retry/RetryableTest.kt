package io.github.manamiproject.modb.core.httpclient.retry

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RetryableTest {

    @Test
    fun `no retry necessary, return the result`() {
        // given
        val expectedResult = HttpResponse(200, EMPTY)
        val retryable = Retryable(RetryBehavior(retryOnResponsePredicate = { false }))

        // when
        val result = retryable.execute { expectedResult }

        // then
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `initial execution fails, 2 retries fail, third -and last- retry is successful`() {
        // given
        val expectedResult = HttpResponse(200, EMPTY)

        var currentAttempt = 0
        val retryable = Retryable(RetryBehavior(
            maxAttempts = 3,
            retryOnResponsePredicate = {
                ++currentAttempt

                when(currentAttempt) {
                    1, 2, 3 -> true
                    4 -> false
                    else -> throw IllegalStateException("too many invocations")
                }

            }
        ))

        // when
        val result = retryable.execute { expectedResult }

        // then
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `throws exception, because it still fails after max attempts retries`() {
        // given
        var currentAttempt = 0
        val retryable = Retryable(RetryBehavior(
            maxAttempts = 3,
            retryOnResponsePredicate = {
                ++currentAttempt

                when(currentAttempt) {
                    1, 2, 3, 4 -> true
                    else -> throw IllegalStateException("too many invocations")
                }

            }
        ))

        // when
        val result = assertThrows<FailedAfterRetryException> {
            retryable.execute { HttpResponse(200, EMPTY) }
        }

        // then
        assertThat(result).hasMessage("Execution failed despite retry attempts.")
    }
}