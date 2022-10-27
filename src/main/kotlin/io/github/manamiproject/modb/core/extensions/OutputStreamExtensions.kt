package io.github.manamiproject.modb.core.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.OutputStream

/**
 * Writes a [String] to an [OutputStream] and flushes it.
 * @since 1.0.0
 * @return The same [OutputStream] which has been used to write the [String]
 */
@Deprecated("Will possibly be removed", ReplaceWith(EMPTY))
public fun OutputStream.write(body: String): OutputStream = runBlocking {// FIXME: still needed?
    writeSuspendable(body)
}

/**
 * Writes a [String] to an [OutputStream] and flushes it.
 * @since 8.0.0
 * @return The same [OutputStream] which has been used to write the [String]
 */
@Deprecated("Will possibly be removed", ReplaceWith(EMPTY))
public suspend fun OutputStream.writeSuspendable(body: String): OutputStream { // FIXME: still needed?
    val outputStream = this

    return withContext(Dispatchers.IO) {
        outputStream.write(body.toByteArray())
        outputStream.flush()
        return@withContext outputStream
    }
}