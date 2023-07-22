package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHeaderCreator.createHeadersFor
import io.github.manamiproject.modb.core.httpclient.HttpProtocol.HTTP_1_1
import io.github.manamiproject.modb.core.httpclient.HttpProtocol.HTTP_2
import io.github.manamiproject.modb.core.httpclient.retry.FailedAfterRetryException
import io.github.manamiproject.modb.core.httpclient.retry.RetryBehavior
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.net.Proxy
import java.net.Proxy.NO_PROXY
import java.net.URL

/**
 * Default HTTP client based on OKHTTP.
 * Handles individual retry behavior for a HTTP request.
 *
 * # Trigger
 * A retry will always execute the request first.
 * If this request failed based on the cases in the given [RetryBehavior] then the request will be retried the number of times
 * defined in [RetryBehavior.maxAttempts]. So for the worst case a request will be executed initial request + [RetryBehavior.maxAttempts] times.
 * **Example:** if [RetryBehavior.maxAttempts] is set to `3` worst case would be `4` executions in total.
 *
 * # Retry
 * + Waits for the amount of time defined in [RetryBehavior.waitDuration]
 * + Checks if some other code has to be executed prior to the retry and executes it.
 * + Executes request again
 * @since 9.0.0
 * @param proxy **Default** is [NO_PROXY]
 * @param protocols List of supported HTTP protocol versions in the order of preference.
 * @param okhttpClient Instance of the OKHTTP client on which this client is based.
 * @param retryBehavior [RetryBehavior] to use for each request.
 * @param isTestContext Whether this runs in the unit test context or not.
 */
public class DefaultHttpClient(
    proxy: Proxy = NO_PROXY,
    private val protocols: List<HttpProtocol> = listOf(HTTP_2, HTTP_1_1),
    private var okhttpClient: Call.Factory = sharedOkHttpClient,
    private val retryBehavior: RetryBehavior = defaultRetryBehavior,
    private val isTestContext: Boolean = false,
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

        executeRetryable(request)
    }

    override suspend fun get(
        url: URL,
        headers: Map<String, Collection<String>>,
    ): HttpResponse = withContext(LIMITED_NETWORK) {
        val requestHeaders = mutableMapOf<String, String>()
        requestHeaders.putAll(createHeadersFor(url, Browser.random()))
        requestHeaders.putAll(headers.mapKeys { it.key.lowercase() }.map { it.key to it.value.joinToString(",") })

        val request = Request.Builder()
            .get()
            .url(url)
            .headers(requestHeaders.toHeaders())
            .build()

        executeRetryable(request)
    }

    private suspend fun executeRetryable(request: Request): HttpResponse = withContext(LIMITED_NETWORK) {
        var attempt = 0
        var response = HttpResponse(0, EMPTY)

        try {
            response = okhttpClient.newCall(request).execute().toHttpResponse()
        } catch (e: Throwable) {
            log.warn { "Initial request resulted in [${e.javaClass.canonicalName}]. Performing retry." }
        }

        while (attempt < retryBehavior.maxAttempts && isActive && (response.code == 0 || retryBehavior.cases.keys.any { it.invoke(response) })) {
            log.info { "Performing retry [${attempt+1}/${retryBehavior.maxAttempts}]" }

            if (!isTestContext) {
                delay(retryBehavior.waitDuration.invoke(attempt).inWholeMilliseconds)
            }

            val currentCase = retryBehavior.cases.keys.find { it.invoke(response) }

            attempt++

            try {
                retryBehavior.cases[currentCase]?.invoke() // invoke executeBeforeRetry
                response = okhttpClient.newCall(request).execute().toHttpResponse()
            } catch (e: Throwable) {
                if (attempt == retryBehavior.maxAttempts) {
                    throw FailedAfterRetryException("Execution failed despite [$attempt] retry attempts.", e)
                } else {
                    log.warn { "[${e.javaClass.canonicalName}] was thrown calling [${request.method} ${request.url}]. Performing retry [${attempt +1}] after waiting time." }
                }
            }
        }

        if (retryBehavior.cases.keys.any { it.invoke(response) }) {
            throw FailedAfterRetryException("Execution failed despite [$attempt] retry attempts. Last invocation returned http status code [${response.code}]")
        }

        return@withContext response
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

    private companion object {
        private val log by LoggerDelegate()
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

private val defaultRetryBehavior = RetryBehavior().apply {
    addCase { it.code in 500..599 }
    addCase { it.code == 425 }
    addCase { it.code == 429 }
}