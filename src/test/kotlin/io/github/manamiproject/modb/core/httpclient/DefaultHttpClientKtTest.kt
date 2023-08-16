package io.github.manamiproject.modb.core.httpclient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.test.MockServerTestCase
import io.github.manamiproject.modb.test.WireMockServerCreator
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Protocol.HTTP_2
import okhttp3.Request
import okhttp3.Response
import okio.Timeout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.test.Test


internal class DefaultHttpClientKtTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Nested
    inner class ConstructorTests {

        @Test
        fun `throws exception if the list of http protocol versions is empty`() {
            // when
            val result = assertThrows<IllegalArgumentException> {
                DefaultHttpClient(
                    protocols = mutableListOf(),
                    isTestContext = true,
                )
            }

            // then
            assertThat(result).hasMessage("Requires at least one http protocol version.")
        }
    }

    @Nested
    inner class GetTests {

        @Test
        fun `successful retrieve response`() {
            runBlocking {
                // given
                val path = "anime/1535"
                val httpResponseCode = 200
                val body = "Successful"

                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path")).willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(httpResponseCode)
                                .withBody(body)
                    )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                ).get(url)

                // then
                assertThat(result.code).isEqualTo(httpResponseCode)
                assertThat(result.body).isEqualTo(body)
            }
        }

        @Test
        fun `receive an error`() {
            runBlocking {
                // given
                val path = "anime/1535"
                val httpResponseCode = 400
                val body = "Bad request"

                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path")).willReturn(
                        aResponse()
                            .withHeader("Content-Type", "text/plain")
                            .withStatus(httpResponseCode)
                            .withBody(body)
                    )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                ).get(url)

                // then
                assertThat(result.code).isEqualTo(httpResponseCode)
                assertThat(result.body).isEqualTo(body)
            }
        }

        @Test
        fun `headers can be overridden - override User-Agent`() {
            runBlocking {
                // given
                val header = mapOf(
                    "User-Agent" to listOf("Test-Agent")
                )

                serverInstance.stubFor(
                    get(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).get(URL("http://localhost:$port/test"), header)

                // then
                serverInstance.verify(
                getRequestedFor(urlEqualTo("/test"))
                    .withHeader("User-Agent", equalTo("Test-Agent"))
                )
            }
        }

        @Test
        fun `add additional header to GET request`() {
            runBlocking {
                // given
                val header = mapOf(
                    "Additional-Header" to listOf("Some value")
                )

                serverInstance.stubFor(
                    get(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).get(
                    url = URL("http://localhost:$port/test"),
                    headers = header,
                )

                // then
                serverInstance.verify(
                    getRequestedFor(urlEqualTo("/test"))
                        .withHeader("Additional-Header", equalTo("Some value"))
                )
            }
        }

        @Test
        fun `multiple values of a header are joined by a comma`() {
            runBlocking {
                // given
                serverInstance.stubFor(
                    get(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                val headers = mapOf("multi-value-key" to listOf("value1", "value2"))

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).get(
                    url = URL("http://localhost:$port/test"),
                    headers = headers,
                )

                // then
                serverInstance.verify(
                    getRequestedFor(urlEqualTo("/test"))
                        .withHeader("multi-value-key", equalTo("value1,value2"))
                )
            }
        }

        @Test
        fun `status code - initial execution fails, all retries fail except the last retry which is successful`() {
            runBlocking {
                // given
                val path = "anime/1535"

                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 1")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 1")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 2")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 2")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 3")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 3")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 4")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 4")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 5")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 5")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(200)
                                .withBody(EMPTY)
                        )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                ).get(url)

                // then
                assertThat(result.code).isEqualTo(200)
            }
        }

        @Test
        fun `status code - throws exception, because it still fails after max attempts retries`() {
            runBlocking {
                // given
                val path = "anime/1535"

                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path")).willReturn(
                        aResponse()
                            .withHeader("Content-Type", "text/plain")
                            .withStatus(429)
                            .withBody(EMPTY)
                    )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = exceptionExpected<FailedAfterRetryException> {
                    DefaultHttpClient(
                        isTestContext = true,
                    ).get(url)
                }

                // then
                assertThat(result).hasMessage("Execution failed despite [5] retry attempts. Last invocation returned http status code [429]")
                assertThat(result.cause).hasNoCause()
            }
        }

        @Test
        fun `exception - initial execution fails, all retries fail except the last retry which is successful`() {
            runBlocking {
                // given
                val url = URL("http://localhost:$port/test")
                val exception = SocketTimeoutException("invoked by test")

                val testCall = object : Call {
                    override fun cancel() = shouldNotBeInvoked()
                    override fun clone(): Call = shouldNotBeInvoked()
                    override fun enqueue(responseCallback: Callback) = shouldNotBeInvoked()
                    override fun isCanceled(): Boolean = shouldNotBeInvoked()
                    override fun isExecuted(): Boolean = shouldNotBeInvoked()
                    override fun request(): Request = shouldNotBeInvoked()
                    override fun timeout(): Timeout = shouldNotBeInvoked()
                    override fun execute(): Response = Response.Builder()
                        .protocol(HTTP_2)
                        .message(EMPTY)
                        .request(
                            Request.Builder()
                                .url(url)
                                .build()
                        )
                        .code(200)
                        .build()
                }

                var currentAttempt = -1
                val testOkHttpClient = Call.Factory {
                    currentAttempt++
                    when (currentAttempt) {
                        5 -> testCall
                        else -> throw exception
                    }
                }

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                    okhttpClient = testOkHttpClient,
                ).get(
                    url = url,
                )

                assertThat(result.code).isEqualTo(200)
            }
        }

        @Test
        fun `exception - throws exception, because it still fails after max attempts retries`() {
            runBlocking {
                // given
                val url = URL("http://localhost:$port/test")
                val exception = SocketTimeoutException("invoked by test")

                val testOkHttpClient = Call.Factory { throw exception }

                // when
                val result = exceptionExpected<FailedAfterRetryException> {
                    DefaultHttpClient(
                        isTestContext = true,
                        okhttpClient = testOkHttpClient,
                    ).get(
                        url = url,
                    )
                }

                assertThat(result).hasMessage("Execution failed despite [5] retry attempts.")
                assertThat(result.cause).hasCause(exception)
            }
        }

        @Test
        fun `always uses first retryable that matches`() {
            runBlocking {
                // given
                val path = "anime/1535"

                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 1")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 1")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(200)
                                .withBody(EMPTY)
                        )
                )

                val url = URL("http://localhost:$port/$path")

                var executedCase = EMPTY
                val testRetryBehavior = RetryBehavior().apply {
                    addCase({ executedCase = "case1"}) { response -> response.code == 429 }
                    addCase({ executedCase = "case2"}) { response -> response.code == 429 }
                    addCase({ executedCase = "case3"}) { response -> response.code == 429 }
                    addCase({ executedCase = "case4"}) { response -> response.code == 429 }
                }

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                    retryBehavior = testRetryBehavior,
                ).get(url)

                // then
                assertThat(result.code).isEqualTo(200)
                assertThat(executedCase).isEqualTo("case1")
            }
        }
    }

    @Nested
    inner class PostTests {

        @Test
        fun `successfully retrieve response`() {
            runBlocking {
                // given
                val path = "graphql"
                val httpResponseCode = 200
                val body = "{ \"key\": \"some-value\" }"

                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path")).willReturn(
                        aResponse()
                            .withStatus(httpResponseCode)
                            .withBody("{{request.body}}")
                    )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = url,
                    headers = mapOf("test-header" to listOf("headervalue")),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = body
                    ),
                )

                // then
                assertThat(result.code).isEqualTo(httpResponseCode)
                assertThat(result.body).isEqualTo("{ &quot;key&quot;: &quot;some-value&quot; }")
            }
        }

        @Test
        fun `receive an error`() {
            runBlocking {
                // given
                val path = "anime/1535"
                val httpResponseCode = 400
                val body = "Bad request"

                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path")).willReturn(
                        aResponse()
                            .withStatus(httpResponseCode)
                            .withBody(body)
                    )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = url,
                    headers = mapOf("test-header" to listOf("headervalue")),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = body
                    )
                )

                // then
                assertThat(result.code).isEqualTo(httpResponseCode)
                assertThat(result.body).isEqualTo(body)
            }
        }

        @Test
        fun `headers can be overridden - override User-Agent`() {
            runBlocking {
                // given
                val header = mapOf(
                    "User-Agent" to listOf("Test-Agent")
                )

                serverInstance.stubFor(
                    post(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = URL("http://localhost:$port/test"),
                    headers = header,
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = "{ \"key\": \"some-value\" }"
                    )
                )

                // then
                serverInstance.verify(
                    postRequestedFor(urlEqualTo("/test"))
                        .withHeader("User-Agent", equalTo("Test-Agent"))
                )
            }
        }

        @Test
        fun `send accept encoding gzip for POST requests`() {
            runBlocking {
                // given
                serverInstance.stubFor(
                    post(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = "{ \"id\": 1 }"
                    )
                )

                // then
                serverInstance.verify(
                    postRequestedFor(urlEqualTo("/test"))
                        .withHeader("accept-encoding", equalTo("gzip"))
                )
            }
        }

        @Test
        fun `headers can be overridden - override content-type`() {
            runBlocking {
                // given
                serverInstance.stubFor(
                    post(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = RequestBody(
                        mediaType = "application/xml",
                        body = "<element>content<element>"
                    )
                )

                // then
                serverInstance.verify(
                    postRequestedFor(urlEqualTo("/test"))
                        .withHeader("content-type", equalTo("application/xml"))
                )
            }
        }

        @Test
        fun `add additional header to POST request`() {
            runBlocking {
                // given
                val header = mapOf(
                    "Additional-Header" to listOf("Some value")
                )

                serverInstance.stubFor(
                    post(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = URL("http://localhost:$port/test"),
                    headers = header,
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = "{ \"id\": 1 }"
                    )
                )

                // then
                serverInstance.verify(
                    postRequestedFor(urlEqualTo("/test"))
                        .withHeader("Additional-Header", equalTo("Some value"))
                )
            }
        }

        @Test
        fun `multiple values of a header are joined by a comma`() {
            runBlocking {
                // given
                serverInstance.stubFor(
                    post(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                val headers = mapOf("multi-value-key" to listOf("value1", "value2"))

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = RequestBody(APPLICATION_JSON, "{ \"property\": \"value\" }"),
                    headers = headers,
                )

                // then
                serverInstance.verify(
                    postRequestedFor(urlEqualTo("/test"))
                        .withHeader("multi-value-key", equalTo("value1,value2"))
                )
            }
        }

        @Test
        fun `body is sent correctly for POST request`() {
            runBlocking {
                // given
                serverInstance.stubFor(
                    post(urlPathEqualTo("/test")).willReturn(
                        aResponse()
                            .withStatus(200)
                    )
                )

                // when
                DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = "{ \"property\": \"value\" }"
                    )
                )

                // then
                serverInstance.verify(
                    postRequestedFor(urlEqualTo("/test"))
                        .withRequestBody(EqualToJsonPattern("{ \"property\": \"value\" }", true, true))
                )
            }
        }

        @Test
        fun `throws exception if media type of the request body is blank`() {
            // given
            val client = DefaultHttpClient(isTestContext = true)
            val requestBody = RequestBody(
                mediaType = EMPTY,
                body = "{ \"property\": \"value\" }",
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                client.post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = requestBody,
                )
            }

            // then
            assertThat(result).hasMessage("MediaType must not be blank.")
        }

        @Test
        fun `throws exception if the body of the request body is blank`() {
            // given
            val client = DefaultHttpClient(isTestContext = true)
            val requestBody = RequestBody(
                mediaType = APPLICATION_JSON,
                body = EMPTY,
            )

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                client.post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = requestBody,
                )
            }

            // then
            assertThat(result).hasMessage("The request's body must not be blank.")
        }

        @Test
        fun `status code - initial execution fails, all retries fail except the last retry which is successful`() {
            runBlocking {
                // given
                val path = "graphql"
                val body = "{ \"key\": \"some-value\" }"

                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 1")
                )
                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 1")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 2")
                )
                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 2")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 3")
                )
                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 3")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 4")
                )
                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 4")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 5")
                )
                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 5")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(200)
                                .withBody(EMPTY)
                        )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                ).post(
                    url = url,
                    headers = mapOf("test-header" to listOf("headervalue")),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = body
                    )
                )

                // then
                assertThat(result.code).isEqualTo(200)
            }
        }

        @Test
        fun `status code - throws exception, because it still fails after max attempts retries`() {
            runBlocking {
                // given
                val path = "graphql"
                val body = "{ \"key\": \"some-value\" }"

                serverInstance.stubFor(
                    post(urlPathEqualTo("/$path")).willReturn(
                        aResponse()
                            .withHeader("Content-Type", "text/plain")
                            .withStatus(429)
                            .withBody(EMPTY)
                    )
                )

                val url = URL("http://localhost:$port/$path")

                // when
                val result = exceptionExpected<FailedAfterRetryException> {
                    DefaultHttpClient(
                        isTestContext = true,
                    ).post(
                        url = url,
                        headers = mapOf("test-header" to listOf("headervalue")),
                        requestBody = RequestBody(
                            mediaType = APPLICATION_JSON,
                            body = body
                        )
                    )
                }

                // then
                assertThat(result).hasMessage("Execution failed despite [5] retry attempts. Last invocation returned http status code [429]")
                assertThat(result.cause).hasNoCause()
            }
        }

        @Test
        fun `exception - initial execution fails, all retries fail except the last retry which is successful`() {
            runBlocking {
                // given
                val url = URL("http://localhost:$port/test")
                val exception = SocketTimeoutException("invoked by test")
                val body = "{ \"key\": \"some-value\" }"

                val testCall = object : Call {
                    override fun cancel() = shouldNotBeInvoked()
                    override fun clone(): Call = shouldNotBeInvoked()
                    override fun enqueue(responseCallback: Callback) = shouldNotBeInvoked()
                    override fun isCanceled(): Boolean = shouldNotBeInvoked()
                    override fun isExecuted(): Boolean = shouldNotBeInvoked()
                    override fun request(): Request = shouldNotBeInvoked()
                    override fun timeout(): Timeout = shouldNotBeInvoked()
                    override fun execute(): Response = Response.Builder()
                        .protocol(HTTP_2)
                        .message(EMPTY)
                        .request(
                            Request.Builder()
                                .url(url)
                                .build()
                        )
                        .code(200)
                        .build()
                }

                var currentAttempt = -1
                val testOkHttpClient = Call.Factory {
                    currentAttempt++
                    when (currentAttempt) {
                        5 -> testCall
                        else -> throw exception
                    }
                }

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                    okhttpClient = testOkHttpClient
                ).post(
                    url = url,
                    headers = mapOf("test-header" to listOf("headervalue")),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = body
                    ),
                )

                assertThat(result.code).isEqualTo(200)
            }
        }

        @Test
        fun `exception - throws exception, because it still fails after max attempts retries`() {
            runBlocking {
                // given
                val url = URL("http://localhost:$port/test")
                val exception = SocketTimeoutException("invoked by test")
                val body = "{ \"key\": \"some-value\" }"

                val testOkHttpClient = Call.Factory { throw exception }

                // when
                val result = exceptionExpected<FailedAfterRetryException> {
                    DefaultHttpClient(
                        isTestContext = true,
                        okhttpClient = testOkHttpClient
                    ).post(
                        url = url,
                        headers = mapOf("test-header" to listOf("headervalue")),
                        requestBody = RequestBody(
                            mediaType = APPLICATION_JSON,
                            body = body
                        ),
                    )
                }

                assertThat(result).hasMessage("Execution failed despite [5] retry attempts.")
                assertThat(result.cause).hasCause(exception)
            }
        }

        @Test
        fun `always uses first retryable that matches`() {
            runBlocking {
                // given
                val path = "anime/1535"

                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs(STARTED)
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(429)
                                .withBody(EMPTY)
                        )
                        .willSetStateTo("Retry 1")
                )
                serverInstance.stubFor(
                    get(urlPathEqualTo("/$path"))
                        .inScenario("Fail until last retry")
                        .whenScenarioStateIs("Retry 1")
                        .willReturn(
                            aResponse()
                                .withHeader("Content-Type", "text/plain")
                                .withStatus(200)
                                .withBody(EMPTY)
                        )
                )

                val url = URL("http://localhost:$port/$path")

                var executedCase = EMPTY
                val testRetryBehavior = RetryBehavior().apply {
                    addCase({ executedCase = "case1"}) { response -> response.code == 429 }
                    addCase({ executedCase = "case2"}) { response -> response.code == 429 }
                    addCase({ executedCase = "case3"}) { response -> response.code == 429 }
                    addCase({ executedCase = "case4"}) { response -> response.code == 429 }
                }

                // when
                val result = DefaultHttpClient(
                    isTestContext = true,
                    retryBehavior = testRetryBehavior,
                ).get(url)

                // then
                assertThat(result.code).isEqualTo(200)
                assertThat(executedCase).isEqualTo("case1")
            }
        }
    }
}