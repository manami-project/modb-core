package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import java.net.URL

public interface HeaderCreator {

    public fun createHeadersFor(
        url: URL,
        browserType: BrowserType = DESKTOP,
    ): Map<String, String>

    public fun createHeadersFor(
        url: URL,
        browser: Browser,
        browserType: BrowserType = DESKTOP,
    ): Map<String, String>
}
