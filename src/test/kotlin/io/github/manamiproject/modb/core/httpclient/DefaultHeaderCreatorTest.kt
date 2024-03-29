package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.Browser.CHROME
import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import org.assertj.core.api.Assertions.assertThat
import java.net.URI
import kotlin.test.Test

internal class DefaultHeaderCreatorTest {

    @Test
    fun `check header for firefox`() {
        // given
        val url = URI("http://localhost:8080").toURL()

        //when
        val result = DefaultHeaderCreator.createHeadersFor(url, FIREFOX)

        // then
        assertThat(result["host"]).isEqualTo("localhost")
        assertThat(result["connection"]).isEqualTo("keep-alive")
        assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
        assertThat(result["accept-language"]).isIn(listOf("en-US,en;q=0.8"))
        assertThat(result["accept"]).isEqualTo("*/*")
        assertThat(result["dnt"]).isEqualTo("1")
        assertThat(result["pragma"]).isEqualTo("no-cache")
        assertThat(result["cache-control"]).isEqualTo("no-cache")
        assertThat(result["te"]).isEqualTo("Trailers")
        assertThat(result["user-agent"]).isIn(
            listOf(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 14.3; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Linux i686; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Linux x86_64; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:122.0) Gecko/20100101 Firefox/122.0",
            )
        )
    }

    @Test
    fun `check header for chrome`() {
        // given
        val url = URI("http://localhost:8080").toURL()

        //when
        val result = DefaultHeaderCreator.createHeadersFor(url, CHROME)

        // then
        assertThat(result["host"]).isEqualTo("localhost")
        assertThat(result["connection"]).isEqualTo("keep-alive")
        assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
        assertThat(result["accept-language"] ).isIn(listOf("en-US,en;q=0.8"))
        assertThat(result["pragma"]).isEqualTo("no-cache")
        assertThat(result["cache-control"]).isEqualTo("no-cache")
        assertThat(result["te"]).isEqualTo("Trailers")
        assertThat(result["user-agent"]).isIn(
            listOf(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            )
        )
    }
}