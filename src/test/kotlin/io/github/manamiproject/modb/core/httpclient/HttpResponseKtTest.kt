package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.EMPTY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class HttpResponseKtTest {

    @Test
    fun `response headers are converted to lower case keys`() {
        // given
        val responseHeaders = mutableMapOf<String, List<String>>().apply {
            put("COOKIE", emptyList())
            put("X-CSRF-TOKEN", emptyList())
        }

        // when
        val result = HttpResponse(
            code = 200,
            body = EMPTY,
            _headers = responseHeaders
        )

        // then
        assertThat(result.headers).containsKey("cookie")
        assertThat(result.headers).doesNotContainKey("COOKIE")
        assertThat(result.headers).containsKey("x-csrf-token")
        assertThat(result.headers).doesNotContainKey("X-CSRF-TOKEN")
    }

    @Nested
    inner class  IsOkTests {

        @Test
        fun `returns true if code is 200`() {
            // given
            val httpResponse = HttpResponse(
                code = 200,
                body = EMPTY
            )

            // when
            val result = httpResponse.isOk()

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns false if code is anything but 200`() {
            // given
            val httpResponse = HttpResponse(
                code = 201,
                body = EMPTY
            )

            // when
            val result = httpResponse.isOk()

            // then
            assertThat(result).isFalse()
        }
    }
}