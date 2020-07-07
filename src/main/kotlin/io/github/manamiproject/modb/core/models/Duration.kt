package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.models.Duration.TimeUnit.*
import java.util.*

/**
 * @since 1.0.0
 */
typealias Seconds = Int

/**
 * Duration of an anime.
 * @since 1.0.0
 * @param value The duration
 * @param unit Indicates whether the [value] refers to seconds, minutes or hours.
 */
data class Duration(
    private val value: Int,
    private val unit: TimeUnit
) {
    /**
     * @since 1.0.0
     * @return Duration in seconds.
     */
    val duration: Seconds
        get() = when (unit) {
            SECONDS -> value
            MINUTES -> value * 60
            HOURS ->  value * 3600
        }

    /**
     * Returns the duration in seconds as formatted [String].
     *
     * **Example output:** `120 seconds`
     * @since 1.0.0
     * @return Value in seconds followed by `seconds`
     */
    override fun toString() = "$duration seconds"

    override fun equals(other: Any?): Boolean {
        if (other !is Duration) {
            return false
        }

        return duration == other.duration
    }

    override fun hashCode() = Objects.hash(duration)

    /**
     * @since 1.0.0
     */
    enum class TimeUnit {
        HOURS,
        MINUTES,
        SECONDS
    }
}