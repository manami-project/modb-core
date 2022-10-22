package io.github.manamiproject.modb.core.httpclient.retry

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RetryableTest {

    @Test
    fun `no retry necessary, return the result`() {
        // given
        val expectedResult = HttpResponse(200, EMPTY)
        val retryable = Retryable(RetryBehavior())

        // when
        val result = runBlocking {
            retryable.executeSuspendable { expectedResult }
        }

        // then
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `initial execution fails, 2 retries fail, third -and last- retry is successful`() {
        // given
        val expectedResult = HttpResponse(200, EMPTY)

        var currentAttempt = 0
        val retryBehavior = RetryBehavior(maxAttempts = 3).apply {
            addCase(
                retryIf = { httpResponse -> httpResponse.code != 200 },
                executeBeforeRetry = {
                    ++currentAttempt

                    if (currentAttempt > 4) {
                        throw IllegalStateException("too many invocations")
                    }
                }
            )
        }
        val retryable = Retryable(retryBehavior)

        // when
        val result = runBlocking {
            retryable.executeSuspendable {
                if (currentAttempt != 3) {
                    HttpResponse(500, EMPTY)
                } else {
                    expectedResult
                }
            }
        }

        // then
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `throws exception, because it still fails after max attempts retries`() {
        // given
        var currentAttempt = 0
        val retryBehavior = RetryBehavior(maxAttempts = 3).apply {
            addCase(
                retryIf = { httpResponse -> httpResponse.code != 200 },
                executeBeforeRetry = {
                    ++currentAttempt

                    if (currentAttempt > 4) {
                        throw IllegalStateException("too many invocations")
                    }
                }
            )
        }
        val retryable = Retryable(retryBehavior)

        // when
        val result = exceptionExpected<FailedAfterRetryException> {
            retryable.executeSuspendable { HttpResponse(500, EMPTY) }
        }

        // then
        assertThat(result).hasMessage("Execution failed despite retry attempts.")
    }

    @Test
    fun `uses only the first matching RetryBehavior`() {
        // given
        val expectedResult = HttpResponse(200, EMPTY)

        var currentAttempt = 0
        val retryBehaviorUsed = mutableListOf<Int>()
        val retryBehavior = RetryBehavior(maxAttempts = 3).apply {
            addCase(
                retryIf = { httpResponse -> httpResponse.code != 200 },
                executeBeforeRetry = {
                    currentAttempt++
                    retryBehaviorUsed.add(1)
                }
            )
            addCase(
                retryIf = { httpResponse -> httpResponse.code != 200 },
                executeBeforeRetry = {
                    currentAttempt++
                    retryBehaviorUsed.add(2)
                }
            )
        }
        val retryable = Retryable(retryBehavior)

        // when
        runBlocking {
            retryable.executeSuspendable {
                if (currentAttempt != 3) {
                    HttpResponse(500, EMPTY)
                } else {
                    expectedResult
                }
            }
        }

        // then
        assertThat(retryBehaviorUsed).containsOnly(1)
    }
}