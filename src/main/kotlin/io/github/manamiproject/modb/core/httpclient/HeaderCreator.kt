package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import java.net.URL

internal interface HeaderCreator {

    fun createHeadersFor(url: URL, browser: Browser = FIREFOX): Map<String, String>
}
