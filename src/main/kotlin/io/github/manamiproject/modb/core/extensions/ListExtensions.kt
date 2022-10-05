package io.github.manamiproject.modb.core.extensions

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.security.SecureRandom

/**
 * Randomizes the order of elements in a [List]
 * @since 1.0.0
 * @return The randomized list
 */
@Deprecated("Use coroutines",
    ReplaceWith("runBlocking { createShuffledListSuspendable() }", "kotlinx.coroutines.runBlocking")
)
public fun <T> List<T>.createShuffledList(): List<T> = runBlocking {
    createShuffledListSuspendable()
}

/**
 * Randomizes the order of elements in a [List]
 * @since 1.0.0
 * @return The randomized list
 */
public suspend fun <T> List<T>.createShuffledListSuspendable(): List<T> {
    val list = this

    return withContext(Default) {

        if (list.isEmpty() || list.size == 1) {
            return@withContext list
        }

        var shuffledList = mutableListOf<T>()

        shuffledList.addAll(list)

        shuffledList.shuffle(SecureRandom())
        shuffledList.shuffle(SecureRandom())
        shuffledList.shuffle(SecureRandom())
        shuffledList.shuffle(SecureRandom())

        while (list.containsExactlyInTheSameOrder(shuffledList)) {
            shuffledList = list.createShuffledListSuspendable().toMutableList()
        }

        return@withContext shuffledList
    }
}

/**
 * Check whether two lists are completely identical including the order of elements.
 * @since 1.0.0
 * @return `true` if both lists are completely identical.
 */
public fun <T> List<T>.containsExactlyInTheSameOrder(otherList: List<T>): Boolean {
    if (this.size != otherList.size) return false

    this.forEachIndexed { index, value ->
        if (otherList[index] != value) {
            return false
        }
    }

    return true
}