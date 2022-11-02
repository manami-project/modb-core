package io.github.manamiproject.modb.core

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_PRETTY_PRINT
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_SERIALIZE_NULL
import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URI

/**
 * Handles serialization and deserialization of objects to/from JSON.
 * @since 1.0.0
 */
public object Json {

    private const val JSON_IDENT = "  "

    @PublishedApi
    internal val moshi: Moshi = Moshi.Builder()
        .add(UriAdapter())
        .add(SortedListStringAdapter())
        .add(SortedListUriAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * Parse a [String] into an object.
     *
     * **WARNING** [Collection]s of a non-nullable type can still contain null.
     * @since 8.0.0
     * @param json Valid JSON as [String]
     * @return Deserialzed JSON as object of given type [T]
     */
    @OptIn(ExperimentalStdlibApi::class)
    public suspend inline fun <reified T> parseJson(json: String): T? = withContext(LIMITED_CPU) {
        return@withContext moshi.adapter<T>().nullSafe().fromJson(json)
    }

    /**
     * Parse an [InputStream] into an object.
     *
     * **WARNING** [Collection]s of a non-nullable type can still contain null.
     * @since 8.0.0
     * @param json Valid JSON as [InputStream]
     * @return Deserialized JSON as object of given type [T]
     */
    @OptIn(ExperimentalStdlibApi::class)
    public suspend inline fun <reified T> parseJson(json: InputStream): T? = withContext(LIMITED_FS) {
        return@withContext moshi.adapter<T>().fromJson(json.bufferedReader().readText())
    }

    /**
     * Serialize any object to JSON.
     * @since 8.0.0
     * @param obj Any object that is supposed to be serialized to JSON.
     * @param options Options that can change the default behavior of the JSON serialization
     * @return Given object serialized in JSON as [String]
     */
    public suspend fun toJson(obj: Any, vararg options: JsonSerializationOptions): String = withContext(LIMITED_CPU) {
        return@withContext configureJsonAdapter(JsonSerializationSettings(options.toSet())).toJson(obj)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun configureJsonAdapter(settings: JsonSerializationSettings): JsonAdapter<Any> {
        var jsonAdapter = moshi.adapter<Any>()

        if (settings.serializeNullActivated) {
            jsonAdapter = jsonAdapter.serializeNulls()
        }

        if (settings.prettyPrintActivated) {
            jsonAdapter = jsonAdapter.indent(JSON_IDENT)
        }

        return jsonAdapter
    }
}

private class UriAdapter {

    @ToJson
    fun toJson(uri: URI): String = uri.toString()

    @FromJson
    fun fromJson(value: String): URI = URI(value)
}

private class SortedListUriAdapter {

    @ToJson
    fun toJson(writer: JsonWriter, sortedList: SortedList<URI>) {
        writer.beginArray()
        sortedList.forEach {
            writer.jsonValue(it.toString())
        }
        writer.endArray()
    }

    @FromJson
    fun fromJson(reader: JsonReader): SortedList<URI> {
        val result = SortedList<URI>()
        reader.beginArray()
        while (reader.hasNext()) {
            result.add(URI(reader.nextString()))
        }
        reader.endArray()
        return result
    }
}

private class SortedListStringAdapter {

    @ToJson
    fun toJson(writer: JsonWriter, sortedList: SortedList<String>) {
        writer.beginArray()
        sortedList.forEach {
            writer.jsonValue(it)
        }
        writer.endArray()
    }

    @FromJson
    fun fromJson(reader: JsonReader): SortedList<String> {
        val result = SortedList<String>()
        reader.beginArray()
        while (reader.hasNext()) {
            result.add(reader.nextString())
        }
        reader.endArray()
        return result
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