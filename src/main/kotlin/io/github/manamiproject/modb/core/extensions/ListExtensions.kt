package io.github.manamiproject.modb.core.extensions

import java.security.SecureRandom

/**
 * Randomizes the order of elements in a [List]
 * @since 1.0.0
 * @return The randomized list
 */
public fun <T> List<T>.createShuffledList(): List<T> {
    if (this.isEmpty() || this.size == 1) {
        return this
    }

    var shuffledList = mutableListOf<T>()

    shuffledList.addAll(this)

    shuffledList.shuffle(SecureRandom())
    shuffledList.shuffle(SecureRandom())
    shuffledList.shuffle(SecureRandom())
    shuffledList.shuffle(SecureRandom())

    while (this.containsExactlyInTheSameOrder(shuffledList)) {
        shuffledList = this.createShuffledList().toMutableList()
    }

    return shuffledList
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

/**
 * Picks a random element from a [List]
 * @since 1.0.0
 * @return A random element from the given list.
 * @throws IllegalStateException if the list is empty
 */
public fun <T> List<T>.pickRandom(): T {
    return when (this.size) {
        0 -> throw IllegalStateException("Cannot pick random element from empty list.")
        1 -> this.first()
        else -> this[SecureRandom().nextInt(this.size).apply { if (this != 0) this - 1 }]
    }
}