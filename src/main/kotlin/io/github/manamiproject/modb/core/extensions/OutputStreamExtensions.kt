package io.github.manamiproject.modb.core.extensions

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.OutputStream

/**
 * Writes a [String] to an [OutputStream] and flushes it.
 * @since 1.0.0
 * @return The same [OutputStream] which has been used to write the [String]
 */
public fun OutputStream.write(body: String): OutputStream = runBlocking {
    writeSuspendable(body)
}

/**
 * Writes a [String] to an [OutputStream] and flushes it.
 * @since 7.3.0
 * @return The same [OutputStream] which has been used to write the [String]
 */
public suspend fun OutputStream.writeSuspendable(body: String): OutputStream {
    val outputStream = this

    return withContext(IO) {
        outputStream.write(body.toByteArray())
        outputStream.flush()
        return@withContext outputStream
    }
}