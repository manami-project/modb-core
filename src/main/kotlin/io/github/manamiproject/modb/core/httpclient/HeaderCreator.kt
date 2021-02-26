package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.pickRandom
import io.github.manamiproject.modb.core.httpclient.Browser.*
import java.net.URL

/**
 * https://www.whatismybrowser.com/guides/the-latest-user-agent/firefox
 * https://www.whatismybrowser.com/guides/the-latest-user-agent/chrome
 */
internal object HeaderCreator {

    private val firefoxUserAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 11.2; rv:86.0) Gecko/20100101 Firefox/86.0",
        "Mozilla/5.0 (X11; Linux i686; rv:86.0) Gecko/20100101 Firefox/86.0",
        "Mozilla/5.0 (Linux x86_64; rv:86.0) Gecko/20100101 Firefox/86.0",
        "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:86.0) Gecko/20100101 Firefox/86.0",
        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:86.0) Gecko/20100101 Firefox/86.0",
        "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:86.0) Gecko/20100101 Firefox/86.0",
    )

    private val chromeUserAgents = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.192 Safari/537.36",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.192 Safari/537.36",
        )

    val userAgents = firefoxUserAgents + chromeUserAgents

    fun createHeadersFor(url: URL, browser: Browser = Firefox): Map<String, String> {
        val headers = mutableMapOf<String, String>(
                Pair("Host", url.host),
                Pair("Connection", "keep-alive"),
                Pair("Upgrade-Insecure-Requests", "1"),
                Pair("Pragma", "no-cache"),
                Pair("Cache-Control", "no-cache"),
                Pair("TE", "Trailers")
        )
        headers.putAll(addBrowserUnspecificHeaders())

        headers.putAll(
                when(browser) {
                    Firefox -> addFirefoxSpecificHeaders()
                    Chromium -> addChromeSpecificHeaders()
                }
        )

        return headers.mapKeys { it.key.toLowerCase() }
    }

    private fun addBrowserUnspecificHeaders(): Map<String, String> {
        val languages = listOf("en-US,en;q=0.8")

        return mapOf(Pair("Accept-Language", languages.pickRandom()))
    }

    private fun addFirefoxSpecificHeaders(): Map<String, String> {
        return mutableMapOf(
                Pair("Accept", "*/*"),
                Pair("DNT", "1"),
                Pair("User-Agent", firefoxUserAgents.pickRandom())
        )
    }

    private fun addChromeSpecificHeaders(): Map<String, String> {
        return mutableMapOf(
            Pair("Accept", "*/*"),
            Pair("User-Agent", chromeUserAgents.pickRandom()),
        )
    }
}