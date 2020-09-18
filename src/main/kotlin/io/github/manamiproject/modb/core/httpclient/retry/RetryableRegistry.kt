package io.github.manamiproject.modb.core.httpclient.retry

import io.github.manamiproject.modb.core.logging.LoggerDelegate

/**
 * Stores [RetryBehavior] using an identifier so that they can be shared easily.
 * @since 1.0.0
 */
public object RetryableRegistry {

    private val log by LoggerDelegate()
    private val retries = mutableMapOf<String, Retryable>()

    /**
     * Register a new [RetryBehavior]
     * @since 1.0.0
     * @param name Unique identifier to be able to retrieve a [Retryable] with the given [RetryBehavior]
     * @param retryBehaviorConfig Config which is stored in the registry and used to create [Retryable]s
     * @return `true` if the regstration was successfully or `false` nothing has been saved, because an entry with the given [name] already exists
     */
    public fun register(name: String, retryBehaviorConfig: RetryBehavior): Boolean {
        return if (retries.containsKey(name)) {
            log.warn("RetryBehaviorConfig named [{}] already exists in repository", name)
            false
        } else {
            log.info("Successfully added [{}] to RetryBehaviorRegistry", name)
            retries[name] = Retryable(retryBehaviorConfig)
            true
        }
    }

    /**
     * @since 1.0.0
     * @param name Identifier of the [RetryBehavior] which should be removed from the registry
     * @return `true` if the entry with the given [name] has been removed or `false` if nothing has been removed, because an entry with
     * the given [name] doesn't exist.
     */
    public fun deregister(name: String): Boolean {
        return if (retries.containsKey(name)) {
            retries.remove(name)
            log.info("RetryBehaviorConfig named [{}] successfully removed.", name)
            true
        } else {
            log.warn("RetryBehaviorConfig named [{}] doesn't exist and therefore couldn't be removed.", name)
            false
        }
    }

    /**
     * Retrieves a [Retryable] based on the [RetryBehavior] which had been registered.
     * @since 1.0.0
     * @param name Identifier under which a [RetryBehavior] has been registered
     * @return A [Retryable] which uses the [RetryBehavior]
     */
    public fun fetch(name: String): Retryable? = retries[name]

    /**
     * Removes all registered entries.
     * @since 1.0.0
     */
    public fun clear(): Unit = retries.clear()
}