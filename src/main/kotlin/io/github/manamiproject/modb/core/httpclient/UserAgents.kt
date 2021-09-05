package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.Browser.CHROME
import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import io.github.manamiproject.modb.core.httpclient.BrowserType.MOBILE
import io.github.manamiproject.modb.core.httpclient.UserAgents.init
import io.github.manamiproject.modb.core.loadResource
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.resourceFileExists
import java.nio.file.Paths
import kotlin.io.path.readLines

/**
 * Provides user agents. Upon creation the initialization takes place. It can be retriggered by calling [init].
 * The object provides hard coded user agents, but you can use manually configured user agents by:
 * + providing a file in the class path
 * + add a property with the path to a file containung user agents
 *
 * The respective file is a simple text file which provides one user agent per line.
 * User agents are separated by [Browser] and by [BrowserType] ([DESKTOP] or [MOBILE]).
 *
 * # Firefox
 * ## configure using files in classpath
 * + **Desktop:** provide _modb-firefox-desktop-user-agents.txt_ in classpath
 * + **Mobile:** provide _modb-firefox-mobile-user-agents.txt_ in classpath
 * ## configure by passing file path using property
 * + **Desktop:** provide _modb.firefox.desktop.user.agents.file_
 * + **Mobile:** provide _modb.firefox.mobile.user.agents.file_
 *
 * # Chome
 * ## configure using files in classpath
 * + **Desktop:** provide _modb-chrome-desktop-user-agents.txt_ in classpath
 * + **Mobile:** provide _modb-chrome-mobile-user-agents.txt_ in classpath
 * ## configure by passing file path using property
 * + **Desktop:** provide _modb.chrome.desktop.user.agents.file_
 * + **Mobile:** provide _modb.chrome.mobile.user.agents.file_
 *
 * You can select each strategy to configure the user agents independently for each combination of [Browser] and [BrowserType].
 *
 * **See also:**
 * + https://www.whatismybrowser.com/guides/the-latest-user-agent/firefox
 * + https://www.whatismybrowser.com/guides/the-latest-user-agent/chrome
 * @since 4.0.0
 */
public object UserAgents {
    
    private val log by LoggerDelegate()

    private const val firefoxDesktopUserAgentsFileName = "modb-firefox-desktop-user-agents.txt"
    private const val firefoxDesktopUserAgentsPropertyName = "modb.firefox.desktop.user.agents.file"
    private var firefoxDesktopUserAgents: Set<String> = emptySet()

    private const val firefoxMobileUserAgentsFileName = "modb-firefox-mobile-user-agents.txt"
    private const val firefoxMobileUserAgentsPropertyName = "modb.firefox.mobile.user.agents.file"
    private var firefoxMobileUserAgents: Set<String> = emptySet()

    private const val chromeDesktopUserAgentsFileName = "modb-chrome-desktop-user-agents.txt"
    private const val chromeDesktopUserAgentsPropertyName = "modb.chrome.desktop.user.agents.file"
    private var chromeDesktopUserAgents: Set<String> = emptySet()

    private const val chromeMobileUserAgentsFileName = "modb-chrome-mobile-user-agents.txt"
    private const val chromeMobileUserAgentsPropertyName = "modb.chrome.mobile.user.agents.file"
    private var chromeMobileUserAgents: Set<String> = emptySet()

    init {
        init()
    }

    /**
     * Reinitializes the lists of user agents.
     * @since 4.0.0
     */
    public fun init() {
        firefoxDesktopUserAgents = firefoxDesktopUserAgents()
        firefoxMobileUserAgents = firefoxMobileUserAgents()
        chromeDesktopUserAgents = chromeDesktopUserAgents()
        chromeMobileUserAgents = chromeMobileUserAgents()
    }

    /**
     * Retrieve a list of user agents.
     * @param browser Selected browser defined by [Browser]
     * @param browserType Determines whether you request an agent for [DESKTOP] or [MOBILE]
     * @since 4.0.0
     */
    public fun userAgents(browser: Browser, browserType: BrowserType): Set<String> {
        return when(browser) {
            FIREFOX -> when(browserType) {
                MOBILE -> firefoxMobileUserAgents
                DESKTOP -> firefoxDesktopUserAgents
            }
            CHROME -> when(browserType) {
                MOBILE -> chromeMobileUserAgents
                DESKTOP -> chromeDesktopUserAgents
            }
        }
    }

    private fun firefoxDesktopUserAgents(): Set<String> {
        log.info { "Initializing user-agents for firefox (desktop)" }

        log.debug { "Checking for user-agent file [$firefoxDesktopUserAgentsFileName] in classpath." }
        if (resourceFileExists(firefoxDesktopUserAgentsFileName)) {
            log.debug { "Found the file [$firefoxDesktopUserAgentsFileName] in classpath" }
            return loadResource(firefoxDesktopUserAgentsFileName).split('\n').toSet()
        }

        log.debug { "Checking for property [$firefoxDesktopUserAgentsPropertyName]" }
        if (System.getProperty(firefoxDesktopUserAgentsPropertyName)?.isNotBlank() == true) {
            log.debug { "Found property [$firefoxDesktopUserAgentsPropertyName]" }
            return Paths.get(System.getProperty(firefoxDesktopUserAgentsPropertyName)).readLines().toSet()
        }

        log.debug { "None of the above could be found. Falling back to hard coded user agents." }

        return setOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11.3; rv:88.0) Gecko/20100101 Firefox/88.0",
            "Mozilla/5.0 (X11; Linux i686; rv:88.0) Gecko/20100101 Firefox/88.0",
            "Mozilla/5.0 (Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0",
            "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:88.0) Gecko/20100101 Firefox/88.0",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0",
            "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0",
        )
    }

    private fun firefoxMobileUserAgents(): Set<String> {
        log.info { "Initializing user-agents for firefox (mobile)" }

        log.debug { "Checking for user-agent file [$firefoxMobileUserAgentsFileName] in classpath." }
        if (resourceFileExists(firefoxMobileUserAgentsFileName)) {
            log.debug { "Found the file [$firefoxMobileUserAgentsFileName] in classpath" }
            return loadResource(firefoxMobileUserAgentsFileName).split('\n').toSet()
        }

        log.debug { "Checking for property [$firefoxMobileUserAgentsPropertyName]" }
        if (System.getProperty(firefoxMobileUserAgentsPropertyName)?.isNotBlank() == true) {
            log.debug {"Found property [$firefoxMobileUserAgentsPropertyName]" }
            return Paths.get(System.getProperty(firefoxMobileUserAgentsPropertyName)).readLines().toSet()
        }

        log.debug { "None of the above could be found. Falling back to hard coded user agents." }

        return setOf(
            "Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/33.0 Mobile/15E148 Safari/605.1.15",
        )
    }

    private fun chromeDesktopUserAgents(): Set<String> {
        log.info { "Initializing user-agents for chrome (desktop)" }

        log.debug { "Checking for user-agent file [$chromeDesktopUserAgentsFileName] in classpath." }
        if (resourceFileExists(chromeDesktopUserAgentsFileName)) {
            log.debug { "Found the file [$chromeDesktopUserAgentsFileName] in classpath" }
            return loadResource(chromeDesktopUserAgentsFileName).split('\n').toSet()
        }

        log.debug { "Checking for property [$chromeDesktopUserAgentsPropertyName]" }
        if (System.getProperty(chromeDesktopUserAgentsPropertyName)?.isNotBlank() == true) {
            log.debug { "Found property [$chromeDesktopUserAgentsPropertyName]" }
            return Paths.get(System.getProperty(chromeDesktopUserAgentsPropertyName)).readLines().toSet()
        }

        log.debug { "None of the above could be found. Falling back to hard coded user agents." }

        return setOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
        )
    }

    private fun chromeMobileUserAgents(): Set<String> {
        log.info { "Initializing user-agents for chrome (mobile)" }

        log.debug { "Checking for user-agent file [$chromeMobileUserAgentsFileName] in classpath." }
        if (resourceFileExists(chromeMobileUserAgentsFileName)) {
            log.debug { "Found the file [$chromeMobileUserAgentsFileName] in classpath" }
            return loadResource(chromeMobileUserAgentsFileName).split('\n').toSet()
        }

        log.debug { "Checking for property [$chromeMobileUserAgentsPropertyName]" }
        if (System.getProperty(chromeMobileUserAgentsPropertyName)?.isNotBlank() == true) {
            log.debug { "Found property [$chromeMobileUserAgentsPropertyName]" }
            return Paths.get(System.getProperty(chromeMobileUserAgentsPropertyName)).readLines().toSet()
        }

        log.debug { "None of the above could be found. Falling back to hard coded user agents." }
        return setOf(
            "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36",
        )
    }
}