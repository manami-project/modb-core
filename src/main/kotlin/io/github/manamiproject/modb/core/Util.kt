package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import java.util.*

/**
 * Creates a random number within the interval of two given numbers. It doesn't matter which of the parameters is
 * the min and which one is the max value. Only restriction is that they cannot be equal.
 * @since 1.0.0
 * @param number1 Bound for a random number (inclusive).
 * @param number2 Bound for a random number (inclusive).
 * @throws IllegalStateException If the given numbers are equal
 * @return A random number withing the given bounds.
 */
public fun random(number1: Int, number2: Int): Long {
    require(number1 != number2) { "Numbers cannot be equal." }

    val min = if (number1 < number2) number1 else number2
    val max = if (number1 > number2) number1 else number2

    return (Random().nextInt((max - min) + 1) + min).toLong()
}

/**
 * Only executes the given function if the current context is not the test context.
 * @since 1.0.0
 * @param config Config of a meta data provider.
 * @param func Function to be executed if the current context is not the test context.
 */
public fun excludeFromTestContext(config: MetaDataProviderConfig, func: () -> Unit) {
    if (!config.isTestContext()) {
        func.invoke()
    }
}