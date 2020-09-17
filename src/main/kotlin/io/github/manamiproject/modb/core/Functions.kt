package io.github.manamiproject.modb.core

import java.io.BufferedReader

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
 * @throws IllegalStateException If the given path does not exist.
 */
fun loadResource(path: String): String {
    require(path.isNotBlank()) { "Given path must not be blank" }

    return ClassLoader.getSystemResourceAsStream(path)?.bufferedReader()
            ?.use(BufferedReader::readText)
            ?.replace(System.getProperty("line.separator"), "\n")
            ?: throw IllegalStateException("Unable to load file [$path]")
}