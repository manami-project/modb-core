package io.github.manamiproject.modb.core.httpclient.retry

import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.httpclient.HttpResponseCode

/**
 * @since 1.0.0
 */
typealias Milliseconds = Long

/**
 * Configuration to individualize the behavior of a [Retryable].
 * Apart from the parameters you can also add additional functions which will be executes before a retry based on the response code.
 * @since 1.0.0
 * @param maxAttempts Number of times a request should be retried before failing completly.
 * @param waitDuration Number milliseconds to wait before the retry is actually executed.
 * @param retryOnResponsePredicate Defines in which case a retry should be performed. The predicate has to return `true` in order to perform a retry.
 */
data class RetryBehavior(
    val maxAttempts: Int = 3,
    val waitDuration: () -> Milliseconds = { 0L },
    val retryOnResponsePredicate: (HttpResponse) -> Boolean
) {

    private val responseCodeBehavior = mutableMapOf<HttpResponseCode, () -> Unit>()

    /**
     * A [Map] having the [HttpResponseCode] on which to execute the function before performing a retry as key and the function to execute as value.
     * @since 1.0.0
     */
    val executeBeforeRetry: Map<HttpResponseCode, () -> Unit>
        get() = responseCodeBehavior.toMap()

    /**
     * @since 1.0.0
     * @param responseCode [HttpResponseCode] on which to execute the function before performing a retry
     * @param executeBeforeRetry Function to execute before performing the retry if the [HttpResponse] contains the given [responseCode]
     * @return `true` if the entry was added successfully or `or` if an entry for the given [responseCode] already exists.
     */
    fun addExecuteBeforeRetryPredicate(responseCode: HttpResponseCode, executeBeforeRetry: () -> Unit): Boolean {
        return if (responseCodeBehavior.containsKey(responseCode)) {
            false
        } else {
            responseCodeBehavior[responseCode] = executeBeforeRetry
            true
        }
    }
}