package io.github.manamiproject.modb.core.extensions

import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

/**
 * Constant for empty [String]
 * @since 1.0.0
 */
public const val EMPTY: String = ""

/**
 * File suffix of lock files indicating that a file is currently being written.
 * @since 1.0.0
 */
public const val LOCK_FILE_SUFFIX: String = "lck"

/**
 * Writes a [String] into a [RegularFile]. If the file already exists it will be overwritten.
 * @since 1.0.0
 * @param file The file to which you want to write the given [String]
 * @param writeLockFile You can choose to write an empty lock file which indicates that the file is currently being created.
 * First the empty lock file is created using [LOCK_FILE_SUFFIX]. Then the actual file is being written. After that the lock file is deleted again.
 * **Default** is `false`.
 * @throws IllegalStateException if the given [String] is blank
 */
public fun String.writeToFile(file: RegularFile, writeLockFile: Boolean = false) {
    check(this.isNotBlank()) { "Trying to write file [$file], but string was blank" }

    val lockFile = file.changeSuffix(LOCK_FILE_SUFFIX)

    if (writeLockFile) {
        lockFile.createFile()
    }

    file.writeText(this)

    if (lockFile.regularFileExists() && writeLockFile) {
        lockFile.deleteIfExists()
    }
}

/**
 * Determines whether a given [String] is a representation of an [Int] or not.
 * This function only checks the syntax. It doesn't check if the value can be safely parsed. So there is no check
 * if the value is within the boundaries of [Int.MIN_VALUE] and [Int.MAX_VALUE].
 * @since 5.2.0
 * @return `true` if the given value offers the general syntax of an [Int]
 */
public fun String.isInt(): Boolean = Regex("[0-9]+").matches(this)

/**
 * Negation of [isInt]
 * @since 5.3.0
 * @see isInt
 * @return `true` if the given value doesn't offer the general syntax of an [Int]
 */
public fun String.isNotInt(): Boolean = !Regex("[0-9]+").matches(this)

/**
 * Removes all occurrences of [value] in a given [String].
 * @since 5.3.0
 * @param value The value that is supposed to be removed from the given [String]
 * @param ignoreCase Whether to operate case sensitive or not. **Default:** `false`
 * @param normalizeWhitespaces If set to true multiple consective whitespaces will be replaced with a single one.
 * @return The [String] without the occurrences of [value]
 */
public fun String.remove(value: String, ignoreCase: Boolean = false, normalizeWhitespaces: Boolean = false): String {
    var cleanedValue = this.replace(value, EMPTY, ignoreCase)

    if (normalizeWhitespaces) {
        cleanedValue = cleanedValue.normalizeWhitespaces()
    }

    return cleanedValue
}

/**
 * Replaces multiple consecutive whitespaces with a single one.
 * @since 5.3.0
 * @return The original [String] having a single whitespace in places where it had multiple consecutive whitespaces before.
 */
public fun String.normalizeWhitespaces(): String = this.replace(Regex(" {2,}"), " ")