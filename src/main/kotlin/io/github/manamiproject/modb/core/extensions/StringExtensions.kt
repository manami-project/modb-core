package io.github.manamiproject.modb.core.extensions

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

    file.write(this)

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