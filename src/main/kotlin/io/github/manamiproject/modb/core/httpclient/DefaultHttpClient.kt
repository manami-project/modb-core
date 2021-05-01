package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHeaderCreator.createHeadersFor
import io.github.manamiproject.modb.core.httpclient.retry.RetryableRegistry
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.ByteString.Companion.encodeUtf8
import java.lang.Thread.sleep
import java.net.Proxy
import java.net.Proxy.NO_PROXY
import java.net.SocketTimeoutException
import java.net.URL

/**
 * @since 1.0.0
 * @param proxy **Default** is [NO_PROXY]
 */
public class DefaultHttpClient(proxy: Proxy = NO_PROXY) : HttpClient {

    private val client = OkHttpClient.Builder()
        .proxy(proxy)
        .retryOnConnectionFailure(true)
        .build()

    override fun post(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse {
        val requestHeaders = mutableMapOf<String, String>()
        requestHeaders.putAll(createHeadersFor(url, Browser.random()))
        requestHeaders.putAll(headers.mapKeys { it.key.toLowerCase() }.map { it.key to it.value.joinToString(",") })
        requestHeaders["content-type"] = requestBody.mediaType

        require(requestBody.mediaType.isNotBlank()) { "MediaType must not be blank." }
        require(requestBody.body.isNotBlank()) { "The request's body must not be blank." }

        val request = Request.Builder()
            .post(requestBody.body.encodeUtf8().toRequestBody(requestBody.mediaType.toMediaType()))
            .url(url)
            .headers(requestHeaders.toHeaders())
            .build()

        return if (retryWith.isNotBlank()) {
            RetryableRegistry.fetch(retryWith)?.execute { executeRequest(request) } ?: throw IllegalStateException("Unable to find retry named [$retryWith]")
        } else {
            executeRequest(request)
        }
    }

    override fun get(url: URL, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse {
        val requestHeaders = mutableMapOf<String, String>()
        requestHeaders.putAll(createHeadersFor(url, Browser.random()))
        requestHeaders.putAll(headers.mapKeys { it.key.toLowerCase() }.map { it.key to it.value.joinToString(",") })

        val request = Request.Builder()
            .get()
            .url(url)
            .headers(requestHeaders.toHeaders())
            .build()

        return if (retryWith.isNotBlank()) {
            RetryableRegistry.fetch(retryWith)?.execute { executeRequest(request) } ?: throw IllegalStateException("Unable to find retry named [$retryWith]")
        } else {
            executeRequest(request)
        }
    }

    override fun executeRetryable(retryWith: String, func: () -> HttpResponse): HttpResponse {
        require(retryWith.isNotBlank()) { "retryWith must not be blank" }
        return RetryableRegistry.fetch(retryWith)?.execute(func) ?: throw IllegalStateException("Unable to find retry named [$retryWith]")
    }

    private fun executeRequest(request: Request): HttpResponse {
        return try {
            client.newCall(request).execute().toHttpResponse()
        } catch(e: SocketTimeoutException) {
            log.warn("SocketTimeoutException calling [${request.method} ${request.url}]. Retry in 5 seconds.")

            sleep(5000)
            executeRequest(request)
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}

private fun Response.toHttpResponse() = HttpResponse(
    code = this.code,
    body = this.body?.string() ?: EMPTY,
    _headers = this.headers.toMultimap().toMutableMap()
)