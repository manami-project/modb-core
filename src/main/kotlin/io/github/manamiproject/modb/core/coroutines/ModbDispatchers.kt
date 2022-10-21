package io.github.manamiproject.modb.core.coroutines

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

public object ModbDispatchers {

    private val availableProcessors = Runtime.getRuntime().availableProcessors()

    /**
     * Coroutine dispatcher backed by a fixed thread pool for a limited number of threads.
     * This dispatcher is supposed to be used for file system operations.
     * @since 8.0.0
     */
    public val LIMITED_FS: ExecutorCoroutineDispatcher by lazy {
        Executors.newFixedThreadPool(availableProcessors) { r ->
            Thread(r, "Filesystem-Dispatcher")
        }.asCoroutineDispatcher()
    }

    /**
     * Coroutine dispatcher backed by a fixed thread pool for a limited number of threads.
     * This dispatcher is supposed to be used for network operations.
     * @since 8.0.0
     */
    public val LIMITED_NETWORK: ExecutorCoroutineDispatcher by lazy {
        Executors.newFixedThreadPool(availableProcessors) { r ->
            Thread(r, "Network-Dispatcher")
        }.asCoroutineDispatcher()
    }

    /**
     * Coroutine dispatcher backed by a fixed thread pool for a limited number of threads.
     * This dispatcher is supposed to be used for CPU intense operations.
     * @since 8.0.0
     */
    public val LIMITED_CPU: ExecutorCoroutineDispatcher by lazy {
        Executors.newFixedThreadPool(availableProcessors) { r ->
            Thread(r, "CPU-Dispatcher")
        }.asCoroutineDispatcher()
    }
}