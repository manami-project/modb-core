package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.extensions.regularFileExists
import java.io.BufferedReader
import java.nio.file.Paths

/**
 * During development: Reads the content of a file from _src/main/resources_ into a [String].
 * After build: Reads the content of a file from _*.jar_ file into a [String].
 * System specific line separators will always be converted to `\n`.
 *
 * **Example**:
 *
 * For _src/main/resources/file.txt_ you can call
 * ```
 * val content = testResource("file.txt")
 * ```
 * For _src/test/resources/dir/subdir/file.txt_ you can call
 * ```
 * val content = testResource("dir/subdir/file.txt")
 * ```
 *
 * @since 2.3.0
 * @return Content of a file as [String]
 * @throws IllegalStateException If the given path does not exist or is not a file.
 */
fun loadResource(path: String): String {
    val resource = ClassLoader.getSystemResource(path) ?: throw IllegalStateException("Given path [$path] does not exist")
    val file = Paths.get(resource.toURI())

    check(file.regularFileExists()) { "Given path [$path] is not a file." }

    return ClassLoader.getSystemResourceAsStream(path)?.bufferedReader()
            ?.use(BufferedReader::readText)
            ?.replace(System.getProperty("line.separator"), "\n")
            ?: throw IllegalStateException("Unable to load file [$path]")
}