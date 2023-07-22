package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHeaderCreator.createHeadersFor
import io.github.manamiproject.modb.core.httpclient.HttpProtocol.HTTP_1_1
import io.github.manamiproject.modb.core.httpclient.HttpProtocol.HTTP_2
import io.github.manamiproject.modb.core.httpclient.retry.RetryableRegistry
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.net.Proxy
import java.net.Proxy.NO_PROXY
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

/**
 * @since 1.0.0
 * @param proxy **Default** is [NO_PROXY]
 * @param protocols List of supported http protocol versions in the order of preference.
 */
public class DefaultHttpClient(
    proxy: Proxy = NO_PROXY,
    private val protocols: List<HttpProtocol> = listOf(HTTP_2, HTTP_1_1),
    private val isTestContext: Boolean = false,
    private var okhttpClient: Call.Factory = sharedOkHttpClient,
) : HttpClient {

    init {
        if (okhttpClient is OkHttpClient) {
            okhttpClient = (okhttpClient as OkHttpClient).newBuilder()
                .protocols(mapHttpProtocols())
                .proxy(proxy)
                .build()
        }
    }

    override suspend fun post(
        url: URL,
        requestBody: RequestBody,
        headers: Map<String, Collection<String>>,
        retryWith: String,
    ): HttpResponse = withContext(LIMITED_NETWORK) {
        val requestHeaders = mutableMapOf<String, String>()
        requestHeaders.putAll(createHeadersFor(url, Browser.random()))
        requestHeaders.putAll(headers.mapKeys { it.key.lowercase() }.map { it.key to it.value.joinToString(",") })
        requestHeaders["content-type"] = requestBody.mediaType

        require(requestBody.mediaType.isNotBlank()) { "MediaType must not be blank." }
        require(requestBody.body.isNotBlank()) { "The request's body must not be blank." }

        val request = Request.Builder()
            .post(requestBody.body.encodeUtf8().toRequestBody(requestBody.mediaType.toMediaType()))
            .url(url)
            .headers(requestHeaders.toHeaders())
            .build()

        if (retryWith.isNotBlank()) {
            executeRetryable(retryWith) {
                executeRequest(request)
            }
        } else {
            executeRequest(request)
        }
    }

    override suspend fun get(
        url: URL,
        headers: Map<String, Collection<String>>,
        retryWith: String,
    ): HttpResponse = withContext(LIMITED_NETWORK) {
        val requestHeaders = mutableMapOf<String, String>()
        requestHeaders.putAll(createHeadersFor(url, Browser.random()))
        requestHeaders.putAll(headers.mapKeys { it.key.lowercase() }.map { it.key to it.value.joinToString(",") })

        val request = Request.Builder()
            .get()
            .url(url)
            .headers(requestHeaders.toHeaders())
            .build()

        if (retryWith.isNotBlank()) {
            executeRetryable(retryWith) {
                executeRequest(request)
            }
        } else {
            executeRequest(request)
        }
    }

    override suspend fun executeRetryable(retryWith: String, func: suspend () -> HttpResponse): HttpResponse = withContext(LIMITED_NETWORK) {
        require(retryWith.isNotBlank()) { "retryWith must not be blank" }
        RetryableRegistry.fetch(retryWith)?.execute(func) ?: throw IllegalStateException("Unable to find retry named [$retryWith]")
    }

    private fun mapHttpProtocols(): List<Protocol> {
        require(protocols.isNotEmpty()) { "Requires at least one http protocol version." }

        return protocols.map {
            when(it) {
                HTTP_2 -> okhttp3.Protocol.HTTP_2
                HTTP_1_1 -> okhttp3.Protocol.HTTP_1_1
            }
        }
    }

    private suspend fun executeRequest(request: Request): HttpResponse = withContext(LIMITED_NETWORK) {
        try {
            okhttpClient.newCall(request).execute().toHttpResponse()
        } catch (e: SocketTimeoutException) {
            log.warn { "SocketTimeoutException calling [${request.method} ${request.url}]. Retry in [${WAIT_BEFORE_RETRY.inWholeSeconds}] seconds." }

            if (!isTestContext) {
                delay(WAIT_BEFORE_RETRY)
            }
            executeRequest(request)
        }
    }

    private companion object {
        private val log by LoggerDelegate()
        private val WAIT_BEFORE_RETRY = 5.toDuration(SECONDS)
    }
}

private fun Response.toHttpResponse() = HttpResponse(
    code = this.code,
    body = this.body?.string() ?: EMPTY,
    _headers = this.headers.toMultimap().toMutableMap()
)

/**
 * Shared [OkHttpClient]. Useful, because this will result in a shared thread pool between different instances of [DefaultHttpClient].
 * [see](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/#customize-your-client-with-newbuilder)
 */
private val sharedOkHttpClient: Call.Factory by lazy {
    OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()
}