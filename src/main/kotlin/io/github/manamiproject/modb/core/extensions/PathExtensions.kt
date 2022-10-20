package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.core.config.FileSuffix
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.*
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.createFile
import kotlin.io.path.Path as PathCreator

/**
 * A [Path] can represent a directory, file or a link. This typealias is used to specify the usage of a directory.
 * @since 1.0.0
 */
public typealias Directory = Path

/**
 * A [Path] can represent a directory, file or a link. This typealias is used to specify the usage of a file.
 * @since 1.0.0
 */
public typealias RegularFile = Path

/**
 * Creates a new [Path] instance with the file's suffix changed. A file is not created by this. If you want to create it
 * call [createFile] afterwards.
 * + If the file is a regular file in form of `name.suffix` the suffix is simply changed to the given value. `test.yaml` => `test.yml`
 * + If the file contains multiple suffix parts (**example:** `test.json.BAK`) only the last part is changed. `test.yml.BAK` => `test.yml.backup`
 * + If the given path is an existing directory, norhing is changed and the given path is returned unchanged. `directory` => `directory`
 * + If a file does not provide a suffix yet, it will be added. `test` => `test.yml`
 * + If the path is a hidden file (files whose whole file name starts with a dot **example:** `.gitignore`) the suffix is simply appended. `.gitignore` => `.gitignore.BAK`
 * @since 1.0.0
 * @param suffix Can be either a suffix with a leading dot (**example:** `.json`) or without a leading dot (**example:** `json`)
 * @return The modified [RegularFile] object
 */
public fun Path.changeSuffix(suffix: String): RegularFile {
    if (this.directoryExists()) {
        return this
    }

    val directory = this.parent
    val fileName = this.fileName.toString()

    val fileNameWithoutSuffix = if (fileName.startsWith('.')) {
        fileName
    } else {
        fileName.substringBeforeLast('.')
    }

    val newSuffix = if (suffix.startsWith(".")) {
        suffix.substring(1)
    } else {
        suffix
    }

    val fileNameRewrite = if (newSuffix.isBlank()) {
        fileNameWithoutSuffix
    } else {
        "$fileNameWithoutSuffix.$newSuffix"
    }

    return if (directory == null) {
        PathCreator(fileNameRewrite)
    } else {
        directory.resolve(fileNameRewrite)
    }
}

/**
 * Checks whether the given [Path] is a regular file and exists.
 * @since 1.0.0
 * @param linkOption Indicating how symbolic links are handled
 * @returns `false` if one of the checks does not match.
 */
public fun Path.regularFileExists(vararg linkOption: LinkOption): Boolean = this.exists(*linkOption) && Files.isRegularFile(this, *linkOption)

/**
 * Checks whether the given [Path] is a directory and exists.
 * @since 1.0.0
 * @param linkOption Indicating how symbolic links are handled
 * @returns `false` if one of the checks does not match.
 */
public fun Path.directoryExists(vararg linkOption: LinkOption): Boolean = this.exists(*linkOption) && Files.isDirectory(this, *linkOption)

/**
 * Read the content of a file to a [String]
 * @since 1.0.0
 * @param charset The charset to use for decoding. **Default** is [UTF_8]
 * @return The file's content
 * @throws NoSuchFileException if the given [Path] doesn't exist or is not a file.
 */
@Deprecated("Use coroutine",
    ReplaceWith("runBlocking { readFileSuspendable(charset) }", "kotlinx.coroutines.runBlocking")
)
public fun Path.readFile(charset: Charset = UTF_8): String = runBlocking {
    readFileSuspendable(charset)
}

/**
 * Read the content of a file to a [String]
 * @since 8.0.0
 * @param charset The charset to use for decoding. **Default** is [UTF_8]
 * @return The file's content
 * @throws NoSuchFileException if the given [Path] doesn't exist or is not a file.
 */
public suspend fun Path.readFileSuspendable(charset: Charset = UTF_8): String = withContext(IO) {
    if (regularFileExists()) {
        readLines(charset).joinToString("\n")
    } else {
        throw NoSuchFileException(this.toString())
    }
}

/**
 * Can copy file to file, directory to directory or a file into a directory.
 * Does not copy the content of directory.
 * @since 1.0.0
 * @param target Target to copy the given [Path] to
 * @param copyOptions Specifying how the copy should be done
 * @return The target as [Path] object
 * @throws FileAlreadyExistsException if the target already exists
 */
@Deprecated("Use coroutine", ReplaceWith(
    "runBlocking { copyToSuspedable(target, *copyOptions) }",
    "kotlinx.coroutines.runBlocking"
)
)
public fun Path.copyTo(target: Path, vararg copyOptions: CopyOption): Path = runBlocking {
    copyToSuspedable(target, *copyOptions)
}

/**
 * Can copy file to file, directory to directory or a file into a directory.
 * Does not copy the content of directory.
 * @since 8.0.0
 * @param target Target to copy the given [Path] to
 * @param copyOptions Specifying how the copy should be done
 * @return The target as [Path] object
 * @throws FileAlreadyExistsException if the target already exists
 */
public suspend fun Path.copyToSuspedable(target: Path, vararg copyOptions: CopyOption): Path {
    val source = this

    return withContext(IO) {
        val processedTarget = when {
            source.regularFileExists() && target.directoryExists() -> target.resolve(source.fileName)
            source.directoryExists() && target.directoryExists() -> target.resolve(source.fileName)
            else -> target
        }

        return@withContext Files.copy(source, processedTarget, *copyOptions)
    }
}

/**
 * Will return the name of either a [RegularFile] or a [Directory].
 * @since 2.2.0
 * @return The file name as a [String]
 */
public fun Path.fileName(): String = this.fileName.toString()

/**
 * + Returns the suffix of a file name in form of `{name}.{suffix}`.
 * + Returns the full file name if there is no dot followed by a suffix.
 * + Returns the full name of a hidden file. A hidden file referring to a file name starting with a dot.
 * **Example:** `.gitignore`
 *
 * Behaves the same for a directory.
 * @since 2.2.0
 * @return Either the file suffix or the full file name
 */
public fun Path.fileSuffix(): FileSuffix {
    val fileName = this.fileName.toString()

    return if (fileName.startsWith('.')) {
        fileName
    } else {
        fileName.substringAfterLast('.')
    }
}