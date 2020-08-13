package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.test.shouldNotBeInvoked
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL

internal class MetaDataProviderConfigKtTest {

    @Test
    fun `isTestContext is false`() {
        // given
        val testConfig = object: MetaDataProviderConfig {
            override fun hostname(): Hostname = shouldNotBeInvoked()
            override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
        }

        // when
        val result = testConfig.isTestContext()

        // then
        assertThat(result).isFalse()
    }

    @Test
    fun `default anime url link is hostname -slash- anime -slash- id`() {
        // given
        val testConfig = object: MetaDataProviderConfig {
            override fun hostname(): Hostname = "example.org"
            override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
        }

        val id = "4hf57"

        // when
        val result = testConfig.buildAnimeLinkUrl(id)

        // then
        assertThat(result).isEqualTo(URL("https://example.org/anime/4hf57"))
    }

    @Test
    fun `buildDataDownloadUrl creates the same url as buildAnimeLinkUrl`() {
        // given
        val testConfig = object: MetaDataProviderConfig {
            override fun hostname(): Hostname = "example.org"
            override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
        }

        val id = "4hf57"

        // when
        val result = testConfig.buildDataDownloadUrl(id)

        // then
        assertThat(result).isEqualTo(testConfig.buildAnimeLinkUrl(id))
    }

    @Nested
    inner class ExtractAnimeIdTests {

        @Test
        fun `throws exception if the url does not contain the hostname of the config`() {
            // given
            val config = object: MetaDataProviderConfig {
                override fun hostname(): Hostname = "example.org"
                override fun buildAnimeLinkUrl(id: AnimeId): URL = URL("https://${hostname()}/some/other/path/$id")
                override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
            }

            // when
            val result = assertThrows<IllegalArgumentException> {
                config.extractAnimeId(URL("https://myanimelist.net/anime/1535"))
            }

            // then
            assertThat(result).hasMessage("URL doesn't contain hostname [example.org]")
        }

        @Test
        fun `correctly extracts anime id`() {
            // given
            val config = object: MetaDataProviderConfig {
                override fun hostname(): Hostname = "example.org"
                override fun buildAnimeLinkUrl(id: AnimeId): URL = URL("https://${hostname()}/some/other/path/$id")
                override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
            }

            val id = "3j4--21f"
            val url = config.buildAnimeLinkUrl(id)

            // when
            val result = config.extractAnimeId(url)

            // then
            assertThat(result).isEqualTo(id)
        }
    }
}