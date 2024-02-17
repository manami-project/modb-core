package io.github.manamiproject.modb.core.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import io.github.manamiproject.modb.core.models.Title

internal class TitleAdapter: JsonAdapter<Title>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Title = reader.nextString()

    @ToJson
    override fun toJson(writer: JsonWriter, value: Title?) {
        requireNotNull(value) { "TitleAdapter is non-nullable, but received null." }
        writer.value(value)
    }
}