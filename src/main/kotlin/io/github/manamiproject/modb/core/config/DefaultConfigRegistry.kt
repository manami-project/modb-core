package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.loadResource
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.resourceFileExists
import kotlinx.coroutines.runBlocking
import org.tomlj.Toml
import org.tomlj.TomlArray
import org.tomlj.TomlParseResult
import org.tomlj.TomlTable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import kotlin.io.path.Path

/**
 * Default implementation of [ConfigRegistry]. Handling is as follows:
 *
 * 1. Loads configuration from `config.toml` if it exists in classpath.
 * 2. Checks if environment variable `modb.core.config.location` is set.
 * If a TOML file exists in this path, then it will be loaded. This possibly overrides configurations from the classpath with the same.
 *
 * If you request a property then the implementation checks if an environment variable with that key exists and returns the value accordingly.
 * If that is not the case then it tries to find the key from the previously loaded config files.
 *
 * The files are loaded once when the class is initialized. The environment variables are checked with each function call.
 *
 * Setting environment variables is not supported for [List] and [Map].
 *
 * Property names are supposed to consist of alphanumeric chars and dots.
 *
 * The implementation includes some possible casts.
 * Example: You can retrieve a value directly as [Long] if call [long] on a property of type [String].
 * Or you could call [list] on a single value which is then returned as a [List] with one value in it.
 * @since 13.0.0
 */
public object DefaultConfigRegistry: ConfigRegistry {

    private val log by LoggerDelegate()
    private val properties = mutableMapOf<String, Any?>()
    private const val CONFIG_FILE = "config.toml"
    public const val ENV_VAR_CONFIG_FILE_PATH: String = "modb.core.config.location"

    init {
        if (resourceFileExists(CONFIG_FILE)) {
            log.info { "Loading config.toml from classpath." }
            runBlocking {
                deserializeToml(loadResource(CONFIG_FILE)).forEach { (key, value) ->
                    properties[key] = value
                }
            }
        }

        val envVar = System.getProperty(ENV_VAR_CONFIG_FILE_PATH, EMPTY)

        if (envVar.isNotEmpty()) {
            val file = Path(envVar)

            if (file.regularFileExists()) {
                log.info { "Loading configuration file from [${file.toAbsolutePath()}]." }
                runBlocking {
                    deserializeToml(file.readFile()).forEach { (key, value) ->
                        properties[key] = value
                    }
                }
            }
        }
    }

    override fun string(key: String): String? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable overrides property from config file: [$key]" }
            }
            return envVar
        }

        if (properties.containsKey(key)) {
            return properties[key]?.toString()
        }

        return null
    }

    override fun long(key: String): Long? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable override property from config file: [$key]" }
            }
            return envVar.toLong()
        }

        if (properties.containsKey(key)) {
            return when (val value = properties[key]) {
                is Long -> value
                else -> value?.toString()?.toLongOrNull()
            }
        }

        return null
    }

    override fun boolean(key: String): Boolean? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable override property from config file: [$key]" }
            }
            return envVar.toBooleanStrictOrNull()
        }

        if (properties.containsKey(key)) {
            return when (val value = properties[key]) {
                is Boolean -> value
                else -> value?.toString()?.toBooleanStrictOrNull()
            }
        }

        return null
    }

    override fun double(key: String): Double? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable override property from config file: [$key]" }
            }
            return envVar.toDouble()
        }

        if (properties.containsKey(key)) {
            return when (val value = properties[key]) {
                is Double -> value
                else -> value?.toString()?.toDoubleOrNull()
            }
        }

        return null
    }

    override fun localDate(key: String): LocalDate? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable override property from config file: [$key]" }
            }
            return LocalDate.parse(envVar)
        }

        if (properties.containsKey(key)) {
            return when (val value = properties[key]) {
                is LocalDate -> value
                else -> parseToLocalDateOrNull(value)
            }
        }

        return null
    }

    override fun localDateTime(key: String): LocalDateTime? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable override property from config file: [$key]" }
            }
            return LocalDateTime.parse(envVar)
        }

        if (properties.containsKey(key)) {
            return when (val value = properties[key]) {
                is LocalDateTime -> value
                else -> parseToLocalDateTimeOrNull(value)
            }
        }

        return null
    }

    override fun offsetDateTime(key: String): OffsetDateTime? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            if (properties.containsKey(key)) {
                log.info { "Environment variable override property from config file: [$key]" }
            }
            return OffsetDateTime.parse(envVar)
        }

        if (properties.containsKey(key)) {
            return when (val value = properties[key]) {
                is OffsetDateTime -> value
                else -> parseToOffsetDateTimeOrNull(value)
            }
        }

        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> list(key: String): List<T>? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            throw IllegalStateException("Environment variable is not supported for property of type list. See [$key]")
        }

        if (properties.containsKey(key)) {
            return when (val entry = properties[key]) {
                is Collection<*> -> entry.toList() as List<T>
                null -> null
                else -> listOf(entry as T)
            }
        }

        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T: Any> map(key: String): Map<String, T>? {
        val envVar = System.getProperty(key, EMPTY)

        if (envVar.isNotEmpty()) {
            throw IllegalStateException("Environment variable is not supported for property of type map. See [$key]")
        }

        if (properties.containsKey(key)) {
            return when (val entry = properties[key]) {
                is Map<*, *> -> entry.toMap() as Map<String, T>
                else -> null
            }
        }

        return null
    }

    @Suppress("UNCHECKED_CAST")
    private fun deserializeToml(fileContent: String): Map<String, Any?> {
        val result: TomlParseResult = Toml.parse(fileContent)

        if (result.errors().isNotEmpty()) {
            throw IllegalStateException("Error parsing TOML:\n${result.errors().joinToString("\n") { "- ${it.message}" }}")
        }

        val ret = mutableMapOf<String, Any>()

        result.dottedEntrySet().filter { it.value != null }.forEach { (key, value) ->
            when (value) {
                is TomlArray -> {
                    when {
                        value.size() == 1 && value[0] is TomlTable -> {
                            (value[0] as TomlTable).entryPathSet().map { entry ->
                                val innerKey = entry.key.removeLast()
                                val newKey = "$key.${entry.key.joinToString(".") { it }}"

                                if (ret.containsKey(newKey)) {
                                    (ret[newKey] as MutableMap<String, Any>)[innerKey] = entry.value
                                } else {
                                    ret[newKey] = mutableMapOf(innerKey to entry.value)
                                }
                            }
                        }
                        else -> ret[key] = value.toList()
                    }
                }
                else -> ret[key] = value
            }
        }

        return ret
    }

    private fun parseToLocalDateOrNull(obj: Any?): LocalDate? {
        return when (obj) {
            null -> null
            else -> try {
                LocalDate.parse(obj.toString())
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }

    private fun parseToLocalDateTimeOrNull(obj: Any?): LocalDateTime? {
        return when (obj) {
            null -> null
            else -> try {
                LocalDateTime.parse(obj.toString())
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }

    private fun parseToOffsetDateTimeOrNull(obj: Any?): OffsetDateTime? {
        return when (obj) {
            null -> null
            else -> try {
                OffsetDateTime.parse(obj.toString())
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }
}