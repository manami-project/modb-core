package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.test.shouldNotBeInvoked
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
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
}