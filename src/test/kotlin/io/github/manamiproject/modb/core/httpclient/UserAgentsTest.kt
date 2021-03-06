package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.httpclient.Browser.*
import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import io.github.manamiproject.modb.core.httpclient.BrowserType.MOBILE
import io.github.manamiproject.modb.test.testResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
                System.setProperty("modb.firefox.desktop.user.agents.file", path.toString())
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
                System.clearProperty("modb.firefox.desktop.user.agents.file")
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(FIREFOX, DESKTOP)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11.3; rv:88.0) Gecko/20100101 Firefox/88.0",
                    "Mozilla/5.0 (X11; Linux i686; rv:88.0) Gecko/20100101 Firefox/88.0",
                    "Mozilla/5.0 (Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:88.0) Gecko/20100101 Firefox/88.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0",
                    "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:88.0) Gecko/20100101 Firefox/88.0",
                )
            }
        }

        @Nested
        inner class MobileTests {

            @Test
            fun `override firefox mobile agents using property`() {
                // given
                val path = testResource("header_creator_tests/firefox-mobile-user-agents-property.txt").toAbsolutePath()
                System.setProperty("modb.firefox.mobile.user.agents.file", path.toString())
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
                System.clearProperty("modb.firefox.mobile.user.agents.file")
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(FIREFOX, MOBILE)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/33.0 Mobile/15E148 Safari/605.1.15",
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
                System.setProperty("modb.chrome.desktop.user.agents.file", path.toString())
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
                System.clearProperty("modb.chrome.desktop.user.agents.file")
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(CHROME, DESKTOP)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
                    "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
                )
            }
        }

        @Nested
        inner class MobileTests {

            @Test
            fun `override chrome mobile agents using property`() {
                // given
                val path = testResource("header_creator_tests/chrome-mobile-user-agents-property.txt").toAbsolutePath()
                System.setProperty("modb.chrome.mobile.user.agents.file", path.toString())
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
                System.clearProperty("modb.chrome.mobile.user.agents.file")
            }

            @Test
            fun `return hard coded fallback`() {
                // given
                UserAgents.init()

                // when
                val result = UserAgents.userAgents(CHROME, MOBILE)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36",
                )
            }
        }
    }
}