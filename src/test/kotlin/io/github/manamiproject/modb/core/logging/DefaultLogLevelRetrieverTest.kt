package io.github.manamiproject.modb.core.logging

import io.github.manamiproject.modb.core.logging.DefaultLogLevelRetriever.Companion.LOG_LEVEL_CONFIG_PROPERTY_NAME
import io.github.manamiproject.modb.core.logging.LogLevel.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class DefaultLogLevelRetrieverTest {

    @AfterEach
    fun afterEach() {
        System.clearProperty(LOG_LEVEL_CONFIG_PROPERTY_NAME)
    }

    @Test
    fun `local override will be used first`() {
        // given
        System.setProperty(LOG_LEVEL_CONFIG_PROPERTY_NAME, "INFO")
        val logLevelRetriever = DefaultLogLevelRetriever(localLogLevelOverride = OFF)

        // when
        val result = logLevelRetriever.logLevel

        // then
        assertThat(result).isEqualTo(OFF)
    }

    @Test
    fun `property will be used if neither local override nor environment variable is not set`() {
        // given
        System.setProperty(LOG_LEVEL_CONFIG_PROPERTY_NAME, "INFO")
        val logLevelRetriever = DefaultLogLevelRetriever()

        // when
        val result = logLevelRetriever.logLevel

        // then
        assertThat(result).isEqualTo(INFO)
    }

    @Test
    fun `if log level is nowhere defined use DEBUG as hard coded default`() {
        // given
        val logLevelRetriever = DefaultLogLevelRetriever()

        // when
        val result = logLevelRetriever.logLevel

        // then
        assertThat(result).isEqualTo(DEBUG)
    }
}