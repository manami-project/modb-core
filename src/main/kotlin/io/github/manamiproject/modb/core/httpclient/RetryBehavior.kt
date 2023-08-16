package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.random
import kotlin.time.Duration
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

/**
 * Configuration to individualize the behavior of [DefaultHttpClient] in case.
 * You can determine when a retry is performed and if an action should take place prior to performing the request again.
 * @since 9.0.0
 * @param maxAttempts Number of times a request should be retried before failing completly.
 * @param waitDuration [Duration] to wait before the retry is actually executed.
 */
public data class RetryBehavior(
    val maxAttempts: Int = 5,
    val waitDuration: (Int) -> Duration = { currentAttempt ->
        (random(120000, 240000) * currentAttempt).toDuration(MILLISECONDS)
    },
) {

    private val retryIfToExecuteBeforeRetry = mutableMapOf<(HttpResponse) -> Boolean, suspend () -> Unit>()

    /**
     * A [Map] having the [HttpResponseCode] on which to execute the function before performing a retry as key and the function to execute as value.
     * @since 9.0.0
     */
    val cases: Map<(HttpResponse) -> Boolean, suspend () -> Unit>
        get() = retryIfToExecuteBeforeRetry.toMap()

    /**
     * Adds cases that describe when to perform a retry and optionally what action to perform before executing the retry.
     * @since 9.0.0
     * @param retryIf The function that defines when to trigger a retry based on a [HttpResponse].
     * @param executeBeforeRetry Function to execute before performing the retry. Default value no operation.
     * @return `true` if the entry was added successfully or `false` if an entry for the given [retryIf] already exists.
     */
    public fun addCase(executeBeforeRetry: suspend () -> Unit = {}, retryIf: (HttpResponse) -> Boolean): Boolean {
        return if (retryIfToExecuteBeforeRetry.containsKey(retryIf)) {
            false
        } else {
            retryIfToExecuteBeforeRetry[retryIf] = executeBeforeRetry
            true
        }
    }
}