package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.pickRandom
import io.github.manamiproject.modb.core.httpclient.Browser.CHROME
import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import java.net.URL

internal object DefaultHeaderCreator: HeaderCreator {

    private val firefoxDesktopUserAgents = UserAgents.userAgents(FIREFOX, DESKTOP)
    private val chromeDesktopUserAgents = UserAgents.userAgents(CHROME, DESKTOP)

    override fun createHeadersFor(url: URL, browser: Browser): Map<String, String> {
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
                    FIREFOX -> addFirefoxSpecificHeaders()
                    CHROME -> addChromeSpecificHeaders()
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
                Pair("User-Agent", firefoxDesktopUserAgents.pickRandom())
        )
    }

    private fun addChromeSpecificHeaders(): Map<String, String> {
        return mutableMapOf(
            Pair("Accept", "*/*"),
            Pair("User-Agent", chromeDesktopUserAgents.pickRandom()),
        )
    }
}