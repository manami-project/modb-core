package io.github.manamiproject.modb.core.logging

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.logging.LogLevel.DEBUG

internal class DefaultLogLevelRetriever(private val localLogLevelOverride: LogLevel? = null): LogLevelRetriever {

    private var _logLevel = retrieveLogLevel()

    override val logLevel: LogLevel
        get() = _logLevel

    private fun retrieveLogLevel(): LogLevel = localLogLevelOverride ?: property() ?: DEBUG

    private fun property(): LogLevel? {
        return LogLevel.of(System.getProperty(LOG_LEVEL_CONFIG_PROPERTY_NAME) ?: EMPTY)
    }

    companion object {
        const val LOG_LEVEL_CONFIG_PROPERTY_NAME = "modb.logging.loglevel"
    }
}