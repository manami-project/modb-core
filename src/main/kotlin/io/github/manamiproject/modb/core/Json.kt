package io.github.manamiproject.modb.core

import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
     * @since 1.0.0
     * @param json Valid JSON as [String]
     * @return Deserialzed JSON as object of given type [T]
     */
    public inline fun <reified T> parseJson(json: String): T? = defaultGson.fromJson(json, T::class.java)

    /**
     * Parse an [InputStream] into an object.
     * @since 1.0.0
     * @param json Valid JSON as [InputStream]
     * @return Deserialized JSON as object of given type [T]
     */
    public inline fun <reified T> parseJson(json: InputStream): T? = this.defaultGson.fromJson(InputStreamReader(json), T::class.java)

    /**
     * Serialize any object to JSON.
     * @since 1.0.0
     * @param obj Any object that is supposed to be serialized to JSON.
     * @return Given object serialized in JSON as [String]
     */
    public fun toJson(obj: Any): String =  defaultGson.toJson(obj)
}
