package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import kotlinx.coroutines.*
import java.nio.file.ClosedWatchServiceException
import java.nio.file.WatchKey
import java.nio.file.WatchService

/**
 * Retrieve [WatchKey]s and prevent [ClosedWatchServiceException] to be thrown. Instead of throwing the exception `null` is being returned.
 * @since 8.0.0
 * @return Either a [WatchKey] or `null`
 */
public suspend fun WatchService.longPoll(): WatchKey? { // FIXME: create custom key in order to prevent nullable return value?
    val watchService = this
    return withContext(LIMITED_FS) {
        try {
            var key: WatchKey? = null
            while (key == null && isActive) {
                yield()
                key = watchService.poll()
            }
            key
        } catch (e: ClosedWatchServiceException) {
            null
        }
    }
}