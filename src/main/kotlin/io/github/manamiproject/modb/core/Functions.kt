package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.extensions.regularFileExists
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.nio.file.Paths
import java.util.*

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
 * @throws IllegalArgumentException If the given path is blank.
 * @throws IllegalStateException If the given path does not exist.
 */
@Deprecated("Use coroutine",
    ReplaceWith("runBlocking { loadResourceSuspendable(path) }", "kotlinx.coroutines.runBlocking")
)
public fun loadResource(path: String): String = runBlocking {
    loadResourceSuspendable(path)
}

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
 * @since 8.0.0
 * @return Content of a file as [String]
 * @throws IllegalArgumentException If the given path is blank.
 * @throws IllegalStateException If the given path does not exist.
 */
public suspend fun loadResourceSuspendable(path: String): String = withContext(LIMITED_FS) {
    require(path.isNotBlank()) { "Given path must not be blank" }

    return@withContext ClassLoader.getSystemResourceAsStream(path)?.bufferedReader()
        ?.use(BufferedReader::readText)
        ?.replace(System.getProperty("line.separator"), "\n")
        ?: throw IllegalStateException("Unable to load file [$path]")
}

/**
 * Checks if a file within the classpath exists.
 *
 * **Example**:
 *
 * For _src/main/resources/file.txt_ you can call
 * ```
 * val result = resourceFileExists("file.txt")
 * ```
 * For _src/test/resources/dir/subdir/file.txt_ you can call
 * ```
 * val result = resourceFileExists("dir/subdir/file.txt")
 * ```
 * @since 4.0.0
 * @throws IllegalArgumentException If the given path is blank.
 * @return **true** if the file exists in classpath
 */
public fun resourceFileExists(path: String): Boolean {
    require(path.isNotBlank()) { "Given path must not be blank" }

    val resource = ClassLoader.getSystemResource(path) ?: return false
    val file = Paths.get(resource.toURI())

    return file.regularFileExists()
}

/**
 * Creates a random number within the interval of two given numbers. It doesn't matter which of the parameters is
 * the min and which one is the max value. Only restriction is that they cannot be equal.
 * @since 1.0.0
 * @param number1 Bound for a random number (inclusive).
 * @param number2 Bound for a random number (inclusive).
 * @throws IllegalStateException If the given numbers are equal
 * @return A random number withing the given bounds.
 */
public fun random(number1: Int, number2: Int): Long {
    require(number1 != number2) { "Numbers cannot be equal." }

    val min = if (number1 < number2) number1 else number2
    val max = if (number1 > number2) number1 else number2

    return (Random().nextInt((max - min) + 1) + min).toLong()
}

/**
 * Only executes the given function if the current context is not the test context.
 * @since 1.0.0
 * @param config Config of a meta data provider.
 * @param func Function to be executed if the current context is not the test context.
 */
public fun excludeFromTestContext(config: MetaDataProviderConfig, func: () -> Unit) {
    if (!config.isTestContext()) {
        func.invoke()
    }
}

/**
 * Only executes the given function if the current context is not the test context.
 * @since 8.0.0
 * @param config Config of a meta data provider.
 * @param func Function to be executed if the current context is not the test context.
 */
public suspend fun excludeFromTestContextSuspendable(config: MetaDataProviderConfig, func: suspend () -> Unit): Unit = withContext(LIMITED_CPU) {
    if (!config.isTestContext()) {
        func.invoke()
    }
}

/**
 * Suspend function wrapper for parsing a [String] into a [Document].
 * @since 8.0.0
 * @param rawHtml Raw HTML as [String]
 * @return [Document]
 */
public suspend fun parseHtml(rawHtml: String): Document = withContext(LIMITED_CPU) {
    require(rawHtml.isNotBlank()) { "HTML must not be blank." }
    Jsoup.parse(rawHtml)
}

/**
 * Parses a [String] into a [Document] on which data can be slected and returned.
 * @since 8.0.0
 * @param rawHtml Raw HTML as [String]
 * @param selector Lets you select data in the DOM
 * @return Selected data. Any type can be used.
 */
public suspend inline fun <reified T> parseHtml(rawHtml: String, noinline selector: suspend (Document) -> T): T = withContext(LIMITED_CPU) {
    require(rawHtml.isNotBlank()) { "HTML must not be blank." }
    selector.invoke(Jsoup.parse(rawHtml))
}