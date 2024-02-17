package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.httpclient.Browser.CHROME
import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import io.github.manamiproject.modb.core.httpclient.BrowserType.MOBILE
import io.github.manamiproject.modb.core.httpclient.UserAgents.init
import io.github.manamiproject.modb.core.loadResource
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.resourceFileExists
import kotlinx.coroutines.runBlocking
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
 * + [https://www.whatismybrowser.com/guides/the-latest-user-agent/firefox](https://www.whatismybrowser.com/guides/the-latest-user-agent/firefox)
 * + [https://www.whatismybrowser.com/guides/the-latest-user-agent/chrome](https://www.whatismybrowser.com/guides/the-latest-user-agent/chrome)
 * @since 4.0.0
 */
public object UserAgents {
    
    private val log by LoggerDelegate()

    /**
     * Name of the resource file in classpath.
     * @since 7.0.0
     */
    public const val FIREFOX_DESKTOP_USER_AGENT_RESOURCE_FILE: String = "modb-firefox-desktop-user-agents.txt"

    /**
     * Name of the property which can be set to contain the path to an alternative file providing user agents.
     * @since 7.0.0
     */
    public const val FIREFOX_DESKTOP_USER_AGENTS_FILE_PROPERTY_NAME: String = "modb.httpclient.useragents.firefox.desktop.path"

    /**
     * Name of the resource file in classpath.
     * @since 7.0.0
     */
    public const val FIREFOX_MOBILE_USER_AGENT_RESOURCE_FILE: String = "modb-firefox-mobile-user-agents.txt"

    /**
     * Name of the property which can be set to contain the path to an alternative file providing user agents.
     * @since 7.0.0
     */
    public const val FIREFOX_MOBILE_USER_AGENT_PROPERTY_NAME: String = "modb.httpclient.useragents.firefox.mobile.path"

    /**
     * Name of the resource file in classpath.
     * @since 7.0.0
     */
    public const val CHROME_DESKTOP_USER_AGENT_RESOURCE_FILE: String = "modb-chrome-desktop-user-agents.txt"

    /**
     * Name of the property which can be set to contain the path to an alternative file providing user agents.
     * @since 7.0.0
     */
    public const val CHROME_DESKTOP_USER_AGENT_PROPERTY_NAME: String = "modb.httpclient.useragents.chrome.desktop.path"

    /**
     * Name of the resource file in classpath.
     * @since 7.0.0
     */
    public const val CHROME_MOBILE_USER_AGENT_RESOURCE_FILE: String = "modb-chrome-mobile-user-agents.txt"

    /**
     * Name of the property which can be set to contain the path to an alternative file providing user agents.
     * @since 7.0.0
     */
    public const val CHROME_MOBILE_USER_AGENT_PROPERTY_NAME: String = "modb.httpclient.useragents.chrome.mobile.path"

    private var firefoxDesktopUserAgents: Set<String> = emptySet()
    private var firefoxMobileUserAgents: Set<String> = emptySet()
    private var chromeDesktopUserAgents: Set<String> = emptySet()
    private var chromeMobileUserAgents: Set<String> = emptySet()

    init {
        init()
    }

    /**
     * Reinitializes the lists of user agents.
     * @since 4.0.0
     */
    public fun init() {
        firefoxDesktopUserAgents = desktopUserAgents(
            browser = FIREFOX,
            browserType = DESKTOP,
            fileName = FIREFOX_DESKTOP_USER_AGENT_RESOURCE_FILE,
            propertyName = FIREFOX_DESKTOP_USER_AGENTS_FILE_PROPERTY_NAME,
            default = setOf(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 14.3; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Linux i686; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Linux x86_64; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:122.0) Gecko/20100101 Firefox/122.0",
                "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:122.0) Gecko/20100101 Firefox/122.0",
            ),
        )
        firefoxMobileUserAgents = desktopUserAgents(
            browser = FIREFOX,
            browserType = MOBILE,
            fileName = FIREFOX_MOBILE_USER_AGENT_RESOURCE_FILE,
            propertyName = FIREFOX_MOBILE_USER_AGENT_PROPERTY_NAME,
            default = setOf(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 14_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/122.0 Mobile/15E148 Safari/605.1.15",
            ),
        )
        chromeDesktopUserAgents = desktopUserAgents(
            browser = CHROME,
            browserType = DESKTOP,
            fileName = CHROME_DESKTOP_USER_AGENT_RESOURCE_FILE,
            propertyName = CHROME_DESKTOP_USER_AGENT_PROPERTY_NAME,
            default = setOf(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            ),
        )
        chromeMobileUserAgents = desktopUserAgents(
            browser = CHROME,
            browserType = MOBILE,
            fileName = CHROME_MOBILE_USER_AGENT_RESOURCE_FILE,
            propertyName = CHROME_MOBILE_USER_AGENT_PROPERTY_NAME,
            default = setOf(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/121.0.6167.171 Mobile/15E148 Safari/604.1",
            ),
        )
    }

    /**
     * Retrieve a list of user agents.
     * @param browser Selected browser defined by [Browser].
     * @param browserType Determines whether you request an agent for [DESKTOP] or [MOBILE].
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

    private fun desktopUserAgents(
        browser: Browser,
        browserType: BrowserType,
        fileName: String,
        propertyName: String,
        default: Set<String>,
    ): Set<String> {
        log.info { "Initializing user-agents for [$browser (${browserType.name})]" }

        log.debug { "Checking for user-agent file [$fileName] in classpath." }

        if (resourceFileExists(fileName)) {
            log.debug { "Found the file [$fileName] in classpath" }
            return runBlocking { loadResource(fileName).split('\n').toSet() }
        }

        log.debug { "Checking for property [$propertyName]" }

        if (System.getProperty(propertyName)?.isNotBlank() == true) {
            log.debug { "Found property [$propertyName]" }

            val file = Paths.get(System.getProperty(propertyName))

            when(file.regularFileExists()) {
                true -> return file.readLines().toSet()
                false -> log.warn { "Property file [${file.toAbsolutePath()}] does not exist or is not a file." }
            }
        }

        log.debug { "None of the above could be found. Falling back to hard coded user agents." }

        return default
    }
}