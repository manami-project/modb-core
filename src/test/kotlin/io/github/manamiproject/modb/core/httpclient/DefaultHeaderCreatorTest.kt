package io.github.manamiproject.modb.core.httpclient

import io.github.manamiproject.modb.core.TestConfigRegistry
import io.github.manamiproject.modb.core.config.ConfigRegistry
import io.github.manamiproject.modb.core.httpclient.Browser.CHROME
import io.github.manamiproject.modb.core.httpclient.Browser.FIREFOX
import io.github.manamiproject.modb.core.httpclient.BrowserType.DESKTOP
import io.github.manamiproject.modb.core.httpclient.BrowserType.MOBILE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import java.net.URI
import kotlin.test.Test

internal class DefaultHeaderCreatorTest {

    @Nested
    inner class CreateHeadersForUrlAndBrowserType {

        @Test
        fun `get header with default user agent for any browser of browser type DESKTOP`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T>? = null
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, DESKTOP)

            // then
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 14.5; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Linux i686; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                )
            )
        }

        @Test
        fun `get header with default user agent for any browser of browser type MOBILE`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T>? = null
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, MOBILE)

            // then
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 14_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/126.0 Mobile/15E148 Safari/605.1.15",
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/126.0.6478.35 Mobile/15E148 Safari/604.1",
                )
            )
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `get header with user agent for any browser of browser type DESKTOP`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T> {
                    return listOf(
                        "agent-desktop1",
                        "agent-desktop2",
                        "agent-desktop3",
                        "agent-desktop4",
                    ) as List<T>
                }
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, DESKTOP)

            // then
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "agent-desktop1",
                    "agent-desktop2",
                    "agent-desktop3",
                    "agent-desktop4",
                )
            )
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `get header with user agent for any browser of browser type MOBILE`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T> {
                    return listOf(
                        "agent-desktop1",
                        "agent-desktop2",
                        "agent-desktop3",
                        "agent-desktop4",
                    ) as List<T>
                }
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, MOBILE)

            // then
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "agent-desktop1",
                    "agent-desktop2",
                    "agent-desktop3",
                    "agent-desktop4",
                )
            )
        }
    }

    @Nested
    inner class CreateHeadersForUrlAndBrowserAndBrowserType {

        @Test
        fun `get header with default user agent for firefox of browser type DESKTOP`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T>? = null
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, FIREFOX, DESKTOP)

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
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 14.5; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Linux i686; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
                    "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0",
                )
            )
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `get header with user agent for firefox of browser type DESKTOP`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T> {
                    return listOf(
                        "firefox-desktop1",
                        "firefox-desktop2",
                        "firefox-desktop3",
                    ) as List<T>
                }
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, FIREFOX, DESKTOP)

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
                    "firefox-desktop1",
                    "firefox-desktop2",
                    "firefox-desktop3",
                )
            )
        }

        @Test
        fun `get header with default user agent for firefox of browser type MOBILE`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T>? = null
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, FIREFOX, MOBILE)

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
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 14_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/126.0 Mobile/15E148 Safari/605.1.15",
                )
            )
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `get header with user agent for firefox of browser type MOBILE`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T> {
                    return listOf(
                        "firefox-mobile1",
                        "firefox-mobile2",
                        "firefox-mobile3",
                    ) as List<T>
                }
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, FIREFOX, MOBILE)

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
                    "firefox-mobile1",
                    "firefox-mobile2",
                    "firefox-mobile3",
                )
            )
        }

        @Test
        fun `get header with default user agent for chrome of browser type DESKTOP`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T>? = null
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, CHROME, DESKTOP)

            // then
            assertThat(result["host"]).isEqualTo("localhost")
            assertThat(result["connection"]).isEqualTo("keep-alive")
            assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
            assertThat(result["accept-language"]).isIn(listOf("en-US,en;q=0.8"))
            assertThat(result["accept"]).isEqualTo("*/*")
            assertThat(result["pragma"]).isEqualTo("no-cache")
            assertThat(result["cache-control"]).isEqualTo("no-cache")
            assertThat(result["te"]).isEqualTo("Trailers")
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36",
                )
            )
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `get header with user agent for chrome of browser type DESKTOP`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T> {
                    return listOf(
                        "chrome-desktop1",
                        "chrome-desktop2",
                        "chrome-desktop3",
                    ) as List<T>
                }
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, CHROME, DESKTOP)

            // then
            assertThat(result["host"]).isEqualTo("localhost")
            assertThat(result["connection"]).isEqualTo("keep-alive")
            assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
            assertThat(result["accept-language"]).isIn(listOf("en-US,en;q=0.8"))
            assertThat(result["accept"]).isEqualTo("*/*")
            assertThat(result["pragma"]).isEqualTo("no-cache")
            assertThat(result["cache-control"]).isEqualTo("no-cache")
            assertThat(result["te"]).isEqualTo("Trailers")
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "chrome-desktop1",
                    "chrome-desktop2",
                    "chrome-desktop3",
                )
            )
        }

        @Test
        fun `get header with default user agent for chrome of browser type MOBILE`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T>? = null
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, CHROME, MOBILE)

            // then
            assertThat(result["host"]).isEqualTo("localhost")
            assertThat(result["connection"]).isEqualTo("keep-alive")
            assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
            assertThat(result["accept-language"]).isIn(listOf("en-US,en;q=0.8"))
            assertThat(result["accept"]).isEqualTo("*/*")
            assertThat(result["pragma"]).isEqualTo("no-cache")
            assertThat(result["cache-control"]).isEqualTo("no-cache")
            assertThat(result["te"]).isEqualTo("Trailers")
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/126.0.6478.35 Mobile/15E148 Safari/604.1",
                )
            )
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `get header with user agent for chrome of browser type MOBILE`() {
            // given
            val url = URI("http://localhost:8080").toURL()
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T : Any> list(key: String): List<T> {
                    return listOf(
                        "chrome-mobile1",
                        "chrome-mobile2",
                        "chrome-mobile3",
                    ) as List<T>
                }
            }
            val headerCreator = DefaultHeaderCreator(
                configRegistry = testConfigRegistry,
            )

            //when
            val result = headerCreator.createHeadersFor(url, CHROME, MOBILE)

            // then
            assertThat(result["host"]).isEqualTo("localhost")
            assertThat(result["connection"]).isEqualTo("keep-alive")
            assertThat(result["upgrade-insecure-requests"]).isEqualTo("1")
            assertThat(result["accept-language"]).isIn(listOf("en-US,en;q=0.8"))
            assertThat(result["accept"]).isEqualTo("*/*")
            assertThat(result["pragma"]).isEqualTo("no-cache")
            assertThat(result["cache-control"]).isEqualTo("no-cache")
            assertThat(result["te"]).isEqualTo("Trailers")
            assertThat(result["user-agent"]).isIn(
                listOf(
                    "chrome-mobile1",
                    "chrome-mobile2",
                    "chrome-mobile3",
                )
            )
        }
    }
}