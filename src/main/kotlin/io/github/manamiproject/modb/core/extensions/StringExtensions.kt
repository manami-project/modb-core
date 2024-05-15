package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import kotlinx.coroutines.withContext
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

/**
 * Constant for empty [String].
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
 * @since 8.0.0
 * @param file The file to which you want to write the given [String].
 * @param writeLockFile You can choose to write an empty lock file which indicates that the file is currently being created.
 * First the empty lock file is created using [LOCK_FILE_SUFFIX]. Then the actual file is being written. After that the lock file is deleted again.
 * **Default** is `false`.
 * @throws IllegalStateException if the given [String] is blank.
 * @receiver Any non-nullable [String].
 */
public suspend fun String.writeToFile(file: RegularFile, writeLockFile: Boolean = false) {
    val content = this

    withContext(LIMITED_FS) {
        check(content.isNotBlank()) { "Tried to write file [$file], but the String was blank." }

        val lockFile = file.changeSuffix(LOCK_FILE_SUFFIX)

        if (writeLockFile) {
            lockFile.createFile()
        }

        file.writeText(content)

        if (lockFile.regularFileExists() && writeLockFile) {
            lockFile.deleteIfExists()
        }
    }
}

/**
 * Removes all occurrences of [value] in a given [String].
 * @since 5.3.0
 * @param value The value that is supposed to be removed from the given [String].
 * @param ignoreCase Whether to operate case sensitive or not. **Default:** `false`.
 * @param normalizeWhitespaces If set to true multiple consective whitespaces will be replaced with a single one.
 * @return The [String] without the occurrences of [value].
 * @receiver Any non-nullable [String].
 */
public fun String.remove(value: String, ignoreCase: Boolean = false, normalizeWhitespaces: Boolean = false): String {
    var cleanedValue = this.replace(value, EMPTY, ignoreCase)

    if (normalizeWhitespaces) {
        cleanedValue = cleanedValue.normalizeWhitespaces()
    }

    return cleanedValue
}

/**
 * Replaces multiple consecutive whitespaces with a single one, trims the string and replaces different types of
 * whitespaces (not line breaks or tabs) to default whitespaces.
 * Replaces:
 * - no-break space
 * - narrow no-break space
 * - zero width no-break space
 * - figure space
 * - mongolian vowel separator
 * - word joiner
 * - zero-width joiner
 * - zero-width non-joiner
 * @since 5.3.0
 * @return The original [String], but trimmed and having a single whitespace in places where it had multiple consecutive whitespaces before and containing only default whitespaces.
 * @receiver Any non-nullable [String].
 */
public fun String.normalizeWhitespaces(): String = this.replace('\u00A0', ' ') // no-break space
    .replace('\u202F', ' ') // narrow no-break space
    .replace('\uFEFF', ' ') // zero width no-break space
    .replace('\u2007', ' ') // figure space
    .replace('\u180E', ' ') // mongolian vowel separator
    .replace('\u2060', ' ') // word joiner
    .replace('\u200D', ' ') // zero-width joiner
    .replace("\u200C", "") // zero-width non-joiner
    .replace(Regex(" {2,}"), " ")
    .trim()


/**
 * Removes line breaks, tabs and normalizes whitespaces.
 * @since 12.1.0
 * @return The original [String] having tabs, carriage return and line feed replaced with whitespaces and all whitespace types replaced with the default.
 * @receiver Any non-nullable [String].
 * @see normalizeWhitespaces
 */
public fun String.normalize(): String = this.replace('\r', ' ')
    .replace('\n', ' ')
    .replace('\t', ' ')
    .normalizeWhitespaces()