package io.github.manamiproject.modb.core.logging

import io.github.manamiproject.modb.core.logging.DefaultLogLevelRetriever.Companion.LOG_LEVEL_CONFIG_PROPERTY_NAME
import kotlin.reflect.KProperty

/**
 * Creates a [Logger] for the class in which it is being instantiated.
 * Implementation uses SLF4J. You use bridges to redirect log statements.
 *
 * Global log level can be set either by setting the environment variable [LOG_LEVEL_CONFIG_PROPERTY_NAME] or the system
 * property with the same name.
 *
 * **Usage:**
 * ```
 * companion object {
 *   private val log by LoggerDelegate()
 * }
 * ```
 * @since 7.0.0
 * @param logLevel Local override for the log level. This log level will only apply to this specific logger instance.
 * If nothing is set then the global configuration will be used.
 */
public class LoggerDelegate(private val logLevel: LogLevel? = null) {

    /**
     * Creates the [Logger] for use in delegation.
     * @since 7.0.0
     * @return Actual instance of the logger to use
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        return ModbLogger(ref = thisRef::class, logLevel = logLevel)
    }
}