package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.core.config.FileSuffix
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.*
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.createFile

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
 * + If the file is a regular file in form of `name.suffix` the suffix is simply changed to the given value.
 * + If the given path is an existing directory, norhing is changed and the given path is returned unchanged.
 * + If a file does not provide a suffix yet, it will be added.
 * + If the path is a hidden file (files whose whole file name starts with a dot **example:** `.gitignore`) the suffix is simply
 * appended.
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
        fileName.split('.').first()
    }

    val newSuffix = if (suffix.startsWith(".")) {
        suffix.substring(1)
    } else {
        suffix
    }

    val fileNameRewrite = "$fileNameWithoutSuffix.$newSuffix"

    return if (directory == null) {
        Paths.get(fileNameRewrite)
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
public fun Path.readFile(charset: Charset = UTF_8): String {
    return if (this.regularFileExists()) {
        this.readLines(charset).joinToString("\n")
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
public fun Path.copyTo(target: Path, vararg copyOptions: CopyOption): Path {
    val processedTarget = when {
        this.regularFileExists() && target.directoryExists() -> target.resolve(this.fileName)
        this.directoryExists() && target.directoryExists() -> target.resolve(this.fileName)
        else -> target
    }

    return Files.copy(this, processedTarget, *copyOptions)
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