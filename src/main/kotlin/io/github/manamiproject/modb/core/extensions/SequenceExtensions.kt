package io.github.manamiproject.modb.core.extensions

/**
 * Executes a function at the end of a [Sequence] if the [Sequence] has at least one element.
 * @since 1.0.0
 * @param function The function which will be executed in case the sequence contains elements up to this step.
 */
inline fun <T> Sequence<T>.doIfNotEmpty(function: () -> Unit) {
    if (this.toList().isNotEmpty()) {
        function.invoke()
    }
}