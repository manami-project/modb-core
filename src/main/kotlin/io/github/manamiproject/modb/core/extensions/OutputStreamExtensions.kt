package io.github.manamiproject.modb.core.extensions

import java.io.OutputStream

/**
 * Writes a [String] to an [OutputStream] and flushes it.
 * @since 1.0.0
 * @return The same [OutputStream] which has been used to write the [String]
 */
fun OutputStream.write(body: String): OutputStream {
    this.write(body.toByteArray())
    this.flush()
    return this
}