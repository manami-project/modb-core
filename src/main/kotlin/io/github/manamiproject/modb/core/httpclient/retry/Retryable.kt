package io.github.manamiproject.modb.core.httpclient.retry

import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import java.lang.Thread.*

/**
 * Handles individual retry behavior for a HTTP request.
 *
 * # Trigger
 * A retry will always execute the request first.
 * If this request failed based on the return value of [RetryBehavior.retryOnResponsePredicate] then the request will be retried the number of times
 * defined in [RetryBehavior.maxAttempts]. So for the worst case a request will be executed initial request + [RetryBehavior.maxAttempts] times.
 * **Example:** if [RetryBehavior.maxAttempts] is set to `3` worst case would be `4` executions in total.
 *
 * # Retry
 * + Waits for the number of milliseconds defined in [RetryBehavior.waitDuration]
 * + Checks if some other code has to be executed first based on the response code.
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
    public fun execute(request: () -> HttpResponse): HttpResponse {
        var response = request.invoke()
        var attempt = 0

        while (attempt < config.maxAttempts && config.retryOnResponsePredicate.invoke(response)) {
            log.info { "Performing retry [${attempt+1}/${config.maxAttempts}]" }

            sleep(config.waitDuration.invoke())
            config.executeBeforeRetry[response.code]?.invoke()
            response = request.invoke()
            attempt++
        }

        if (config.retryOnResponsePredicate.invoke(response)) {
            throw FailedAfterRetryException("Execution failed despite retry attempts.")
        }

        return response
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}