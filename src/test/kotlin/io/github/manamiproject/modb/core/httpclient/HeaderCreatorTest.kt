package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.Browser.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URL

internal class HeaderCreatorTest {

    @Test
    fun `check header for firefox`() {
        //when
        val result = HeaderCreator.createHeadersFor(URL("http://localhost:8080"), Firefox)

        // then
        assertThat(result["host"]).isEqualTo("localhost")
        assertThat(result["connection"]).isEqualTo("keep-alive")
        assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
        assertThat(result["accept-language"] in listOf("de,en-US;q=0.7,en;q=0.3", "en-US,en;q=0.5", "en-US,en;q=0.8")).isTrue()
        assertThat(result["accept"]).isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        assertThat(result["dnt"]).isEqualTo("1")
        assertThat(result["user-agent"] in listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:78.0) Gecko/20100101 Firefox/78.0",
            "Mozilla/5.0 (X11; Linux i686; rv:78.0) Gecko/20100101 Firefox/78.0",
            "Mozilla/5.0 (Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0",
            "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:78.0) Gecko/20100101 Firefox/78.0",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0",
            "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0"
        )).isTrue()
    }

    @Test
    fun `check header for chromium`() {
        //when
        val result = HeaderCreator.createHeadersFor(URL("http://localhost:8080"), Chromium)

        // then
        assertThat(result["host"]).isEqualTo("localhost")
        assertThat(result["connection"]).isEqualTo("keep-alive")
        assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
        assertThat(result["accept-language"] in listOf("de,en-US;q=0.7,en;q=0.3", "en-US,en;q=0.5", "en-US,en;q=0.8")).isTrue()
        assertThat(result["user-agent"] in listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36"
        )).isTrue()
    }
}