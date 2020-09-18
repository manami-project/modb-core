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