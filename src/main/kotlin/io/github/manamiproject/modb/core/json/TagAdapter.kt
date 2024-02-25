package io.github.manamiproject.modb.core.json

import com.squareup.moshi.*
import io.github.manamiproject.modb.core.models.Tag

internal class TagAdapter: JsonAdapter<Tag>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Tag = reader.nextString()

    @ToJson
    override fun toJson(writer: JsonWriter, value: Tag?) {
        requireNotNull(value) { "TagAdapter is non-nullable, but received null." }
        writer.value(value)
    }
}