package io.github.manamiproject.modb.core.httpclient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.retry.FailedAfterRetryException
import io.github.manamiproject.modb.core.httpclient.retry.RetryBehavior
import io.github.manamiproject.modb.core.httpclient.retry.RetryableRegistry
import io.github.manamiproject.modb.test.MockServerTestCase
import io.github.manamiproject.modb.test.WireMockServerCreator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL

internal class DefaultHttpClientKtTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @AfterEach
    override fun afterEach() {
        serverInstance.stop()
        RetryableRegistry.clear()
    }

    @Nested
    inner class GetTests {

        @Test
        fun `successful retrieve response via http method GET`() {
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
            val result = DefaultHttpClient().get(url)

            // then
            assertThat(result.code).isEqualTo(httpResponseCode)
            assertThat(result.body).isEqualTo(body)
        }

        @Test
        fun `receive an error retrieve response via http method GET`() {
            // given
            val path = "anime/1535"
            val httpResponseCode = 500
            val body = "Internal Server Error"

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
            val result = DefaultHttpClient().get(url)

            // then
            assertThat(result.code).isEqualTo(httpResponseCode)
            assertThat(result.body).isEqualTo(body)
        }

        @Test
        fun `headers can be overridden using GET - override User-Agent`() {
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
            DefaultHttpClient().get(URL("http://localhost:$port/test"), header)

            // then
            serverInstance.verify(
                getRequestedFor(urlEqualTo("/test"))
                    .withHeader("User-Agent", equalTo("Test-Agent"))
            )
        }

        @Test
        fun `add additional header to GET request`() {
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
            DefaultHttpClient().get(URL("http://localhost:$port/test"), header)

            // then
            serverInstance.verify(
                getRequestedFor(urlEqualTo("/test"))
                    .withHeader("Additional-Header", equalTo("Some value"))
            )
        }

        @Test
        fun `multiple values of a header are joined by a comma`() {
            // given
            serverInstance.stubFor(
                get(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            val headers = mapOf("multi-value-key" to listOf("value1", "value2"))

            // when
            DefaultHttpClient().get(
                url = URL("http://localhost:$port/test"),
                headers = headers
            )

            // then
            serverInstance.verify(
                getRequestedFor(urlEqualTo("/test"))
                    .withHeader("multi-value-key", equalTo("value1,value2"))
            )
        }

        @Test
        fun `throws exception if the retry behavior can't be found`() {
            // when
            val result = assertThrows<IllegalStateException> {
                DefaultHttpClient().get(
                    url = URL("http://localhost:$port/test"),
                    retryWith = "unknown"
                )
            }

            assertThat(result).hasMessage("Unable to find retry named [unknown]")
        }

        @Test
        fun `successfully executes and returns result being decorated with retry`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior(
                retryOnResponsePredicate = { false }
            )

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            serverInstance.stubFor(
                get(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            val result = DefaultHttpClient().get(
                url = URL("http://localhost:$port/test"),
                retryWith = testRetryBehaviorName
            )

            assertThat(result.code).isEqualTo(200)
            assertThat(result.body).isEmpty()
        }

        @Test
        fun `throws exception if execution failed despite retry attempts`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior(
                retryOnResponsePredicate = { true }
            )

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            serverInstance.stubFor(
                get(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            val result = assertThrows<FailedAfterRetryException> {
                DefaultHttpClient().get(
                    url = URL("http://localhost:$port/test"),
                    retryWith = testRetryBehaviorName
                )
            }

            assertThat(result).hasMessage("Execution failed despite retry attempts.")
        }
    }

    @Nested
    inner class PostTests {

        @Test
        fun `successfully retrieve response via http method POST`() {
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
            val result = DefaultHttpClient().post(
                url = url,
                headers = mapOf("test-header" to listOf("headervalue")),
                requestBody = RequestBody(
                    mediaType = APPLICATION_JSON,
                    body = body
                )
            )

            // then
            assertThat(result.code).isEqualTo(httpResponseCode)
            assertThat(result.body).isEqualTo("{ &quot;key&quot;: &quot;some-value&quot; }")
        }

        @Test
        fun `headers can be overridden using POST - override User-Agent`() {
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
            DefaultHttpClient().post(
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

        @Test
        fun `send accept encoding gzip for POST requests`() {
            // given
            serverInstance.stubFor(
                post(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            DefaultHttpClient().post(
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

        @Test
        fun `headers can be overridden using POST - override content-type`() {
            // given
            serverInstance.stubFor(
                post(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            DefaultHttpClient().post(
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

        @Test
        fun `add additional header to POST request`() {
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
            DefaultHttpClient().post(
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

        @Test
        fun `body is sent correctly for POST request`() {
            // given
            serverInstance.stubFor(
                post(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            DefaultHttpClient().post(
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

        @Test
        fun `throws exception if media type of the request body is blank`() {
            // given
            val client = DefaultHttpClient()
            val requestBody = RequestBody(
                mediaType = EMPTY,
                body = "{ \"property\": \"value\" }"
            )

            // when
            val result = assertThrows<IllegalArgumentException> {
                client.post(
                        url = URL("http://localhost:$port/test"),
                        requestBody = requestBody
                    )
            }

            // then
            assertThat(result).hasMessage("MediaType must not be blank.")
        }

        @Test
        fun `throws exception if the body of the request body is blank`() {
            // given
            val client = DefaultHttpClient()
            val requestBody = RequestBody(
                mediaType = APPLICATION_JSON,
                body = EMPTY
            )

            // when
            val result = assertThrows<IllegalArgumentException> {
                client.post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = requestBody
                )
            }

            // then
            assertThat(result).hasMessage("The request's body must not be blank.")
        }

        @Test
        fun `multiple values of a header are joined by a comma`() {
            // given
            serverInstance.stubFor(
                post(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            val headers = mapOf("multi-value-key" to listOf("value1", "value2"))

            // when
            DefaultHttpClient().post(
                url = URL("http://localhost:$port/test"),
                requestBody = RequestBody(APPLICATION_JSON, "{ \"property\": \"value\" }"),
                headers = headers
            )

            // then
            serverInstance.verify(
                postRequestedFor(urlEqualTo("/test"))
                    .withHeader("multi-value-key", equalTo("value1,value2"))
            )
        }

        @Test
        fun `throws exception if the retry behavior can't be found`() {
            // when
            val result = assertThrows<IllegalStateException> {
                DefaultHttpClient().post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = RequestBody(APPLICATION_JSON, "{ \"property\": \"value\" }"),
                    retryWith = "unknown"
                )
            }

            assertThat(result).hasMessage("Unable to find retry named [unknown]")
        }

        @Test
        fun `successfully executes and returns result being decorated with retry`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior(
                retryOnResponsePredicate = { false }
            )

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            serverInstance.stubFor(
                post(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            val result = DefaultHttpClient().post(
                url = URL("http://localhost:$port/test"),
                requestBody = RequestBody(APPLICATION_JSON, "{ \"property\": \"value\" }"),
                retryWith = testRetryBehaviorName
            )

            assertThat(result.code).isEqualTo(200)
            assertThat(result.body).isEmpty()
        }

        @Test
        fun `throws exception if execution failed despite retry attempts`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior(
                retryOnResponsePredicate = { true }
            )

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            serverInstance.stubFor(
                post(urlPathEqualTo("/test")).willReturn(
                    aResponse()
                        .withStatus(200)
                )
            )

            // when
            val result = assertThrows<FailedAfterRetryException> {
                DefaultHttpClient().post(
                    url = URL("http://localhost:$port/test"),
                    requestBody = RequestBody(APPLICATION_JSON, "{ \"property\": \"value\" }"),
                    retryWith = testRetryBehaviorName
                )
            }

            assertThat(result).hasMessage("Execution failed despite retry attempts.")
        }
    }

    @Nested
    inner class ExecuteRetryableTests {

        @Test
        fun `throws exception if execution failed despite retry attempts`() {
            // given
            val testRetryBehaviorName = "test"

            val retryBehavior = RetryBehavior(
                retryOnResponsePredicate = { true }
            )

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // when
            val result = assertThrows<FailedAfterRetryException> {
                DefaultHttpClient().executeRetryable(testRetryBehaviorName) {
                    HttpResponse(200, EMPTY)
                }
            }

            assertThat(result).hasMessage("Execution failed despite retry attempts.")
        }

        @Test
        fun `successfully executes and returns result`() {
            // given
            val testRetryBehaviorName = "test"
            val expectedResult = HttpResponse(200, EMPTY)

            val retryBehavior = RetryBehavior(
                retryOnResponsePredicate = { false }
            )

            RetryableRegistry.register(testRetryBehaviorName, retryBehavior)

            // when
            val result = DefaultHttpClient().executeRetryable(testRetryBehaviorName) {
                expectedResult
            }

            assertThat(result).isEqualTo(expectedResult)
        }

        @Test
        fun `throws exception if the retry can't be found`() {
            // when
            val result = assertThrows<IllegalStateException> {
                DefaultHttpClient().executeRetryable("test") {
                    HttpResponse(200, EMPTY)
                }
            }

            assertThat(result).hasMessage("Unable to find retry named [test]")
        }

        @Test
        fun `throws exception if the name of the RetryBehavior is blank`() {
            // when
            val result = assertThrows<IllegalArgumentException> {
                DefaultHttpClient().executeRetryable("       ") {
                    HttpResponse(200, EMPTY)
                }
            }

            assertThat(result).hasMessage("retryWith must not be blank")
        }

        @Test
        fun `throws exception if the name of the RetryBehavior is empty`() {
            // when
            val result = assertThrows<IllegalArgumentException> {
                DefaultHttpClient().executeRetryable("") {
                    HttpResponse(200, EMPTY)
                }
            }

            assertThat(result).hasMessage("retryWith must not be blank")
        }
    }
}