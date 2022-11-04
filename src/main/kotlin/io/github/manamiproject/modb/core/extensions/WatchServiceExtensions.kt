package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.random
import kotlinx.coroutines.*
import java.nio.file.*
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

/**
 * Retrieve [WatchKey]s and prevent [ClosedWatchServiceException] to be thrown. Instead of throwing the exception `null` is being returned.
 * @since 8.0.0
 * @return Either a [WatchKey] or `null`
 * @receiver Any [WatchService]
 */
public suspend fun WatchService.longPoll(): WatchKey? {
    val watchService = this
    return withContext(LIMITED_FS) {
        try {
            var key: WatchKey? = null
            var delay = random(3, 6).toDuration(SECONDS)
            while (key == null && isActive) {
                if (delay < 30.toDuration(SECONDS)) delay = delay.plus(1.toDuration(SECONDS))
                delay(delay)
                key = watchService.poll()
            }
            key
        } catch (e: ClosedWatchServiceException) {
            null
        }
    }
}