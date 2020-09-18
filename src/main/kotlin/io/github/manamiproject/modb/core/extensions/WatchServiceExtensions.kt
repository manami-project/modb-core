package io.github.manamiproject.modb.core.extensions

import java.nio.file.ClosedWatchServiceException
import java.nio.file.WatchKey
import java.nio.file.WatchService

/**
 * Retrieve [WatchKey]s and prevent [ClosedWatchServiceException] to be thrown. Instead of throwing the exception `null` is being returned.
 * @since 1.0.0
 * @return Either a [WatchKey] or `null`
 */
public fun WatchService.takeOrNull(): WatchKey? {
    return try {
        this.take()
    } catch (e: ClosedWatchServiceException) {
        null
    }
}