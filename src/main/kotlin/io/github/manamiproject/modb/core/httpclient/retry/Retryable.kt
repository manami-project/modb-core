package io.github.manamiproject.modb.core.httpclient.retry

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.*

/**
 * Handles individual retry behavior for a HTTP request.
 *
 * # Trigger
 * A retry will always execute the request first.
 * If this request failed based on one of the cases of the [RetryBehavior] then the request will be retried the number of times
 * defined in [RetryBehavior.maxAttempts]. So for the worst case a request will be executed initial request + [RetryBehavior.maxAttempts] times.
 * **Example:** if [RetryBehavior.maxAttempts] is set to `3` worst case would be `4` executions in total.
 *
 * # Retry
 * + Waits for the number of milliseconds defined in [RetryBehavior.waitDuration]
 * + Checks if some other code has to be executed before performing the retry.
 * + Executes request again
 * @since 1.0.0
 * @param config Configuration which individualizes the retry behavior
 * @throws FailedAfterRetryException if the request has been retried the [RetryBehavior.maxAttempts] number of times, but failed anyway.
 */
public class Retryable(private val config: RetryBehavior) {

    /**
     * Executes a request and retries it if necessary.
     * @since 1.0.0
     * @param request Lambda which performs a HTTP request returning a [HttpResponse]
     * @return The actual [HttpResponse] of the request if the request was successful
     */
    @Deprecated("Use coroutine instead", ReplaceWith(
        "runBlocking { executeSuspendable { request.invoke() } }",
        "kotlinx.coroutines.runBlocking"
        )
    )
    public fun execute(request: () -> HttpResponse): HttpResponse {
        return runBlocking {
            executeSuspendable {
                request.invoke()
            }
        }
    }

    /**
     * Executes a request and retries it if necessary.
     * @since 8.0.0
     * @param request Lambda which performs a HTTP request returning a [HttpResponse]
     * @return The actual [HttpResponse] of the request if the request was successful
     */
    public suspend fun executeSuspendable(request: suspend () -> HttpResponse): HttpResponse = withContext(LIMITED_NETWORK) {
        var response = request.invoke()
        var attempt = 0

        while (attempt < config.maxAttempts && isActive && config.cases.keys.any { it.invoke(response) }) {
            log.info { "Performing retry [${attempt+1}/${config.maxAttempts}]" }

            delay(config.waitDuration.invoke().inWholeMilliseconds) // FIXME: exclude from test context

            val currentCase = config.cases.keys.find { it.invoke(response) }
            config.cases[currentCase]?.invoke() // invoke executeBeforeRetry
            response = request.invoke() // perform retry
            attempt++
        }

        if (config.cases.keys.any { it.invoke(response) }) {
            throw FailedAfterRetryException("Execution failed despite retry attempts.")
        }

        return@withContext response
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}