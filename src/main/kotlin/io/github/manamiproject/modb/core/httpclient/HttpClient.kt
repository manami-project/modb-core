package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.retry.RetryBehavior
import io.github.manamiproject.modb.core.httpclient.retry.Retryable
import java.net.URL

/**
 * Defines a HTTP client.
 * @since 1.0.0
 */
public interface HttpClient {

    /**
     * Performs a HTTP POST request with a [RequestBody]
     * @since 8.0.0
     * @param url The actual [URL] that you want to call
     * @param requestBody Contains the the payload as well as the media type which will be automatically applied as content-type header
     * @param headers Optional header parameters. Entries of this map will either add or override header parameters.
     * The key is the name of the header parameter. The value is a list of values for the respective parameters. The values will be joined together.
     * **Default** is an empty [Map].
     * @param retryWith Name of the [RetryBehavior] which should be used. An empty [String] indicates that the request should be done without a retry.
     * **Default**: is an empty [String]
     * @return The server's response.
     */
    public suspend fun post(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>> = emptyMap(), retryWith: String = EMPTY): HttpResponse

    /**
     * Performs a HTTP GET request.
     * @since 8.0.0
     * @param url The actual URL that you want to call
     * @param headers Optional header parameters. Entries of this map will either add or override header parameters.
     * The key is the name of the header parameter. The value is a list of values for the respective parameters. The values will be joined together.
     * **Default** is an empty [Map].
     * @param retryWith Name of the [RetryBehavior] which should be used. An empty [String] indicates that the request should be done without a retry.
     * **Default**: is an empty [String]
     * @return The server's response.
     */
    public suspend fun get(url: URL, headers: Map<String, Collection<String>> = emptyMap(), retryWith: String = EMPTY): HttpResponse

    /**
     * Automatically performs a lookup for a specific [Retryable] and performs a lambda using it.
     * In comparison to [post] or [get] you can have multiple statements within the retry context.
     * This especially comes in handy if you have to dynamically change data in each request like settings tokens for
     * example.
     * @since 8.0.0
     * @param retryWith Name of the [Retryable] which should be used for this request. The name must not be blank.
     * @param func Function which performs a request and returns an [HttpResponse]
     * @return The actual HTTP response from a successful attempt
     * @throws IllegalStateException if a [RetryBehavior] hasn't been registered with the given [retryWith]
     * @throws IllegalArgumentException if [retryWith] is blank
     */
    public suspend fun executeRetryable(retryWith: String, func: suspend () -> HttpResponse): HttpResponse
}