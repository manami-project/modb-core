package io.github.manamiproject.modb.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_PRETTY_PRINT
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_SERIALIZE_NULL
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Handles serialization and deserialization of objects to/from JSON.
 * @since 1.0.0
 */
public object Json {

    @PublishedApi
    internal val defaultGson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    /**
     * Parse a [String] into an object.
     *
     * **WARNING** Due to the underlying implementation it is possible that non-nullable kotlin types can contain `null`
     * as value. Instead of checking non-nullable kotlin types for `null` which will trigger compiler messages you can
     * keep the non-nullable types and call `copy()`. This will immediately throw a `NullPointerException`
     * in case one of the non-nullable types contains `null`.
     * Any type of [Collection] is not affected by this. If the [Collection] contains `null` then no exception will be
     * thrown.
     * Alternatively you can always use nullable types on targets..
     * @since 1.0.0
     * @param json Valid JSON as [String]
     * @return Deserialzed JSON as object of given type [T]
     */
    public inline fun <reified T> parseJson(json: String): T? = defaultGson.fromJson(json, T::class.java)

    /**
     * Parse an [InputStream] into an object.
     *
     * **WARNING** Due to the underlying implementation it is possible that non-nullable kotlin types can contain `null`
     * as value. Instead of checking non-nullable kotlin types for `null` which will trigger compiler messages you can
     * keep the non-nullable types and call `copy()`. This will immediately throw a `NullPointerException`
     * in case one of the non-nullable types contains `null`.
     * Any type of [Collection] is not affected by this. If the [Collection] contains `null` then no exception will be
     * thrown.
     * Alternatively you can always use nullable types on targets.
     * @since 1.0.0
     * @param json Valid JSON as [InputStream]
     * @return Deserialized JSON as object of given type [T]
     */
    public inline fun <reified T> parseJson(json: InputStream): T? = defaultGson.fromJson(InputStreamReader(json), T::class.java)

    /**
     * Serialize any object to JSON.
     * @since 1.0.0
     * @param obj Any object that is supposed to be serialized to JSON.
     * @param options Options that can change the default behavior of the JSON serialization
     * @return Given object serialized in JSON as [String]
     */
    public fun toJson(obj: Any, vararg options: JsonSerializationOptions): String {
        if (options.isEmpty()) {
            return defaultGson.toJson(obj)
        }

        return configureGsonBuilder(GsonBuilder(), JsonSerializationSettings(options.toSet())).create().toJson(obj)
    }

    private fun configureGsonBuilder(gsonBuilder: GsonBuilder, settings: JsonSerializationSettings): GsonBuilder {
        if (settings.serializeNullActivated) {
            gsonBuilder.serializeNulls()
        }

        if (settings.prettyPrintActivated) {
            gsonBuilder.setPrettyPrinting()
        }

        return gsonBuilder
    }
}

/**
 * Possible options to customize serialization of JSON documents using [Json.toJson]
 * @since 7.0.0
 */
public enum class JsonSerializationOptions {
    /**
     * By default the output JSON string is formatted. Using this option will create a minified string instead.
     * @since 7.0.0
     */
    DEACTIVATE_PRETTY_PRINT,
    /**
     * By default a property providing `null` as value will be serialized. Using this option will omit these properties.
     * @since 7.0.0
     */
    DEACTIVATE_SERIALIZE_NULL,
}

private class JsonSerializationSettings(val options: Set<JsonSerializationOptions>) {
    /** Prettyprint is activated by default */
    val prettyPrintActivated: Boolean = false.takeIf { options.contains(DEACTIVATE_PRETTY_PRINT)} ?: true
    /** Serialize null is activated by default */
    val serializeNullActivated: Boolean = false.takeIf { options.contains(DEACTIVATE_SERIALIZE_NULL)} ?: true
}