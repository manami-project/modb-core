package io.github.manamiproject.modb.core.coroutines

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.logging.Logger
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit.SECONDS

/**
 * Provides functionality for managing coroutines.
 * @since 8.0.0
 */
public object CoroutineManager {

    @PublishedApi
    internal val log: Logger by LoggerDelegate()

    /**
     * Wraps [runBlocking] and executes an [action] in a try-catch block. In case any [Throwable] is thrown it will be
     * logged and the dispatcher pools from [ModbDispatchers] will be shut down. Because of that it cannot be used
     * in unit tests. It would make the tests fail with a task rejected exception, because the global dispatchers
     * have already been shut down.
     * @since 8.0.0
     * @param action Any function containing suspend function calls.
     */
    public inline fun <reified T> runCoroutine(noinline action: suspend CoroutineScope.() -> T): T = runBlocking {
        try {
            action.invoke(this)
        } catch(e: Throwable) {
            log.error(e) { "An error occurred!" }
            throw e
        } finally {
            log.info { "Shutting down coroutine dispatchers." }
            setOf(LIMITED_NETWORK, LIMITED_CPU, LIMITED_FS).forEach {
                log.debug { "$it" }
                it.close()
                (it.asExecutor() as ExecutorService).awaitTermination(10, SECONDS)
            }
        }
    }
}