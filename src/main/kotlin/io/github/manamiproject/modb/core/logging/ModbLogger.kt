package io.github.manamiproject.modb.core.logging

import io.github.manamiproject.modb.core.logging.LogLevel.*
import kotlin.reflect.KClass

internal class ModbLogger(
    ref: KClass<*>,
    private val logLevel: LogLevel? = null,
    private val delegate: Logger = Slf4jLogger(ref = ref),
    private val logLevelRetriever: LogLevelRetriever = DefaultLogLevelRetriever(logLevel),
): Logger {

    override fun error(message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(ERROR)) {
            delegate.error { message.invoke() }
        }
    }

    override fun error(exception: Throwable, message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(ERROR)) {
            delegate.error { "${message.invoke()}\n${exception.stackTraceToString()}" }
        }
    }

    override fun warn(message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(WARN)) {
            delegate.warn { message.invoke() }
        }
    }

    override fun warn(exception: Throwable, message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(WARN)) {
            delegate.warn { "${message.invoke()}\n${exception.stackTraceToString()}" }
        }
    }

    override fun info(message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(INFO)) {
            delegate.info { message.invoke() }
        }
    }

    override fun debug(message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(DEBUG)) {
            delegate.debug { message.invoke() }
        }
    }

    override fun trace(message: () -> String) {
        if (logLevelRetriever.logLevel.containsLogLevel(TRACE)) {
            delegate.trace { message.invoke() }
        }
    }
}