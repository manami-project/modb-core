package io.github.manamiproject.modb.core.httpclient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.HttpProtocol.HTTP_1_1
import io.github.manamiproject.modb.test.MockServerTestCase
import io.github.manamiproject.modb.test.WireMockServerCreator
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.Protocol.HTTP_2
import okio.Timeout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.net.*
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

                val url = URI("http://localhost:$port/$path").toURL()

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

                val url = URI("http://localhost:$port/$path").toURL()

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
                ).get(URI("http://localhost:$port/test").toURL(), header)

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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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

                val url = URI("http://localhost:$port/$path").toURL()

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

                val url = URI("http://localhost:$port/$path").toURL()

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
        fun `throws SocketTimeoutException and is retried exactly once returning 200 on retry`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/test").toURL()
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
                        1 -> testCall
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
        fun `throws SocketTimeoutException and rethrows is, because SocketTimeoutException will be tried only once`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/test").toURL()
                val exception = SocketTimeoutException("invoked by test")

                var currentAttempt = -1
                val testOkHttpClient = Call.Factory {
                    currentAttempt++
                    when {
                        currentAttempt < 2 -> throw exception
                        else -> shouldNotBeInvoked()
                    }
                }

                // when
                val result = exceptionExpected<SocketTimeoutException> {
                    DefaultHttpClient(
                        isTestContext = true,
                        okhttpClient = testOkHttpClient,
                    ).get(
                        url = url,
                    )
                }

                assertThat(result).hasMessage("invoked by test")
            }
        }

        @Test
        fun `exception - any exception except SocketTimeoutException is thrown as-is`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/test").toURL()
                val exception = UnknownHostException("invoked by test")

                val testOkHttpClient = Call.Factory { throw exception }

                // when
                val result = exceptionExpected<UnknownHostException> {
                    DefaultHttpClient(
                        isTestContext = true,
                        okhttpClient = testOkHttpClient,
                    ).get(
                        url = url,
                    )
                }

                assertThat(result).hasMessage("invoked by test")
            }
        }

        @Test
        fun `status code 103 - leads to removing HTTP 2 from protocols and performs a retry`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/anime/1535").toURL()

                val testCall103 = object : Call {
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
                        .code(103)
                        .build()
                }

                val testCall200 = object : Call {
                    override fun cancel() = shouldNotBeInvoked()
                    override fun clone(): Call = shouldNotBeInvoked()
                    override fun enqueue(responseCallback: Callback) = shouldNotBeInvoked()
                    override fun isCanceled(): Boolean = shouldNotBeInvoked()
                    override fun isExecuted(): Boolean = shouldNotBeInvoked()
                    override fun request(): Request = shouldNotBeInvoked()
                    override fun timeout(): Timeout = shouldNotBeInvoked()
                    override fun execute(): Response = Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .message(EMPTY)
                        .request(
                            Request.Builder()
                                .url(url)
                                .build()
                        )
                        .code(200)
                        .build()
                }

                var currentAttempt = 0
                val testOkHttpClient = Call.Factory {
                    currentAttempt++
                    when (currentAttempt) {
                        1 -> testCall103
                        else -> testCall200
                    }
                }

                val defaultHttpClient = DefaultHttpClient(
                    okhttpClient = testOkHttpClient,
                    isTestContext = true,
                )

                // when
                val result = defaultHttpClient.get(url)

                // then
                assertThat(result.code).isEqualTo(200)
                val protocols = defaultHttpClient.javaClass.getDeclaredField("protocols")
                protocols.isAccessible = true
                val value = protocols.get(defaultHttpClient) as (ArrayList<*>)
                assertThat(value).containsExactlyInAnyOrder(HTTP_1_1)
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

                val url = URI("http://localhost:$port/$path").toURL()

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
                    post(urlPathEqualTo("/$path")).willReturn(
                        aResponse()
                            .withStatus(httpResponseCode)
                            .withBody(body)
                    )
                )

                val url = URI("http://localhost:$port/$path").toURL()

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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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
                    url = URI("http://localhost:$port/test").toURL(),
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

                val url = URI("http://localhost:$port/$path").toURL()

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

                val url = URI("http://localhost:$port/$path").toURL()

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
        fun `throws SocketTimeoutException and is retried exactly once returning 200 on retry`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/test").toURL()
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
                        1 -> testCall
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
        fun `throws SocketTimeoutException and rethrows is, because SocketTimeoutException will be tried only once`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/test").toURL()
                val exception = SocketTimeoutException("invoked by test")
                val body = "{ \"key\": \"some-value\" }"

                var currentAttempt = -1
                val testOkHttpClient = Call.Factory {
                    currentAttempt++
                    when {
                        currentAttempt < 2 -> throw exception
                        else -> shouldNotBeInvoked()
                    }
                }

                // when
                val result = exceptionExpected<SocketTimeoutException> {
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

                assertThat(result).hasMessage("invoked by test")
            }
        }

        @Test
        fun `exception - any exception except SocketTimeoutException is thrown as-is`() {
            runBlocking {
                // given
                val url = URI("http://localhost:$port/test").toURL()
                val exception = UnknownHostException("invoked by test")
                val body = "{ \"key\": \"some-value\" }"

                val testOkHttpClient = Call.Factory { throw exception }

                // when
                val result = exceptionExpected<UnknownHostException> {
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

                assertThat(result).hasMessage("invoked by test")
            }
        }

        @Test
        fun `status code 103 - leads to removing HTTP 2 from protocols and performs a retry`() {
            runBlocking {
                // given
                val body = "{ \"key\": \"some-value\" }"
                val url = URI("http://localhost:$port/graphql").toURL()

                val testCall103 = object : Call {
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
                        .code(103)
                        .build()
                }

                val testCall200 = object : Call {
                    override fun cancel() = shouldNotBeInvoked()
                    override fun clone(): Call = shouldNotBeInvoked()
                    override fun enqueue(responseCallback: Callback) = shouldNotBeInvoked()
                    override fun isCanceled(): Boolean = shouldNotBeInvoked()
                    override fun isExecuted(): Boolean = shouldNotBeInvoked()
                    override fun request(): Request = shouldNotBeInvoked()
                    override fun timeout(): Timeout = shouldNotBeInvoked()
                    override fun execute(): Response = Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .message(EMPTY)
                        .request(
                            Request.Builder()
                                .url(url)
                                .build()
                        )
                        .code(200)
                        .build()
                }

                var currentAttempt = 0
                val testOkHttpClient = Call.Factory {
                    currentAttempt++
                    when (currentAttempt) {
                        1 -> testCall103
                        else -> testCall200
                    }
                }

                val defaultHttpClient = DefaultHttpClient(
                    okhttpClient = testOkHttpClient,
                    isTestContext = true,
                )

                // when
                val result = defaultHttpClient.post(
                    url = url,
                    headers = mapOf("test-header" to listOf("headervalue")),
                    requestBody = RequestBody(
                        mediaType = APPLICATION_JSON,
                        body = body
                    )
                )

                // then
                assertThat(result.code).isEqualTo(200)
                val protocols = defaultHttpClient.javaClass.getDeclaredField("protocols")
                protocols.isAccessible = true
                val value = protocols.get(defaultHttpClient) as (ArrayList<*>)
                assertThat(value).containsExactlyInAnyOrder(HTTP_1_1)
            }
        }
    }
}