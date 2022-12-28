package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.Browser.CHROME
import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import io.github.manamiproject.modb.core.httpclient.BrowserType.MOBILE
import io.github.manamiproject.modb.core.httpclient.UserAgents.CHROME_DESKTOP_USER_AGENT_PROPERTY_NAME
import io.github.manamiproject.modb.core.httpclient.UserAgents.CHROME_MOBILE_USER_AGENT_PROPERTY_NAME
import io.github.manamiproject.modb.core.httpclient.UserAgents.FIREFOX_DESKTOP_USER_AGENTS_FILE_PROPERTY_NAME
import io.github.manamiproject.modb.core.httpclient.UserAgents.FIREFOX_MOBILE_USER_AGENT_PROPERTY_NAME
import io.github.manamiproject.modb.test.testResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test

internal class UserAgentsTest {

    @AfterEach
    fun afterEach() {
        UserAgents.init()
    }

    @Nested
    inner class FirefoxTests {

        @Nested
        inner class DesktopTests {

            @Test
            fun `override firefox desktop agents using property`() {
                // given
                val path = testResource("header_creator_tests/firefox-desktop-user-agents-property.txt").toAbsolutePath()
                System.setProperty(FIREFOX_DESKTOP_USER_AGENTS_FILE_PROPERTY_NAME, path.toString())
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(FIREFOX, DESKTOP)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "firefox-desktop-agent-1",
                    "firefox-desktop-agent-2",
                    "firefox-desktop-agent-3",
                    "firefox-desktop-agent-4",
                )
                System.clearProperty(FIREFOX_DESKTOP_USER_AGENTS_FILE_PROPERTY_NAME)
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(FIREFOX, DESKTOP)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 13.1; rv:108.0) Gecko/20100101 Firefox/108.0",
                    "Mozilla/5.0 (X11; Linux i686; rv:108.0) Gecko/20100101 Firefox/108.0",
                    "Mozilla/5.0 (X11; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:108.0) Gecko/20100101 Firefox/108.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
                    "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:108.0) Gecko/20100101 Firefox/108.0",
                )
            }
        }

        @Nested
        inner class MobileTests {

            @Test
            fun `override firefox mobile agents using property`() {
                // given
                val path = testResource("header_creator_tests/firefox-mobile-user-agents-property.txt").toAbsolutePath()
                System.setProperty(FIREFOX_MOBILE_USER_AGENT_PROPERTY_NAME, path.toString())
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(FIREFOX, MOBILE)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "firefox-mobile-agent-1",
                    "firefox-mobile-agent-2",
                    "firefox-mobile-agent-3",
                    "firefox-mobile-agent-4",
                )
                System.clearProperty(FIREFOX_MOBILE_USER_AGENT_PROPERTY_NAME)
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(FIREFOX, MOBILE)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 13_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/108.0 Mobile/15E148 Safari/605.1.15",
                )
            }
        }
    }

    @Nested
    inner class ChromeTests {

        @Nested
        inner class DesktopTests {

            @Test
            fun `override chrome desktop agents using property`() {
                // given
                val path = testResource("header_creator_tests/chrome-desktop-user-agents-property.txt").toAbsolutePath()
                System.setProperty(CHROME_DESKTOP_USER_AGENT_PROPERTY_NAME, path.toString())
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(CHROME, DESKTOP)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "chrome-desktop-agent-A",
                    "chrome-desktop-agent-B",
                    "chrome-desktop-agent-C",
                    "chrome-desktop-agent-D",
                )
                System.clearProperty(CHROME_DESKTOP_USER_AGENT_PROPERTY_NAME)
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(CHROME, DESKTOP)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36",
                )
            }
        }

        @Nested
        inner class MobileTests {

            @Test
            fun `override chrome mobile agents using property`() {
                // given
                val path = testResource("header_creator_tests/chrome-mobile-user-agents-property.txt").toAbsolutePath()
                System.setProperty(CHROME_MOBILE_USER_AGENT_PROPERTY_NAME, path.toString())
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(CHROME, MOBILE)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "chrome-mobile-agent-A",
                    "chrome-mobile-agent-B",
                    "chrome-mobile-agent-C",
                    "chrome-mobile-agent-D",
                )
                System.clearProperty(CHROME_MOBILE_USER_AGENT_PROPERTY_NAME)
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(CHROME, MOBILE)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 16_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/108.0.5359.112 Mobile/15E148 Safari/604.1",
                )
            }
        }
    }
}