package io.github.manamiproject.modb.core.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

/**
 * Creates a slf4j [Logger].
 *
 * **Usage:**
 * ```
 * companion object {
 *   private val log by LoggerDelegate()
 * }
 * ```
 * @since 1.0.0
 */
class LoggerDelegate {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        return LoggerFactory.getLogger(thisRef::class.java)
    }
}