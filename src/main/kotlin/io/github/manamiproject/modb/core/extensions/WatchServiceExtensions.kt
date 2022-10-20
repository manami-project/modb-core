package io.github.manamiproject.modb.core.extensions

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.ClosedWatchServiceException
import java.nio.file.WatchKey
import java.nio.file.WatchService

/**
 * Retrieve [WatchKey]s and prevent [ClosedWatchServiceException] to be thrown. Instead of throwing the exception `null` is being returned.
 * @since 1.0.0
 * @return Either a [WatchKey] or `null`
 */
@Deprecated("Use coroutines", ReplaceWith("runBlocking { takeOrNullSuspendable() }", "kotlinx.coroutines.runBlocking"))
public fun WatchService.takeOrNull(): WatchKey? = runBlocking {
    takeOrNullSuspendable()
}

/**
 * Retrieve [WatchKey]s and prevent [ClosedWatchServiceException] to be thrown. Instead of throwing the exception `null` is being returned.
 * @since 8.0.0
 * @return Either a [WatchKey] or `null`
 */
public suspend fun WatchService.takeOrNullSuspendable(): WatchKey? {
    return withContext(IO) {
         try {
            take()
        } catch (e: ClosedWatchServiceException) {
            null
        }
    }
}