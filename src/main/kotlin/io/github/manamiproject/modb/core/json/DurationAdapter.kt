package io.github.manamiproject.modb.core.json

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.models.Duration
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.SECONDS

internal class DurationAdapter: JsonAdapter<Duration>() {

    @FromJson
    override fun fromJson(reader: JsonReader): Duration {
        reader.beginObject()

        var value = 0
        var valueDeserialized = false
        var unit = EMPTY
        var unitDeserialized = false

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "value" -> {
                    value = reader.nextInt()
                    valueDeserialized = true
                }
                "unit" -> {
                    unit = reader.nextString()
                    unitDeserialized = true
                }
                else -> reader.skipValue()
            }
        }

        reader.endObject()

        when {
            !valueDeserialized -> throw IllegalStateException("Property 'value' is either missing or null.")
            !unitDeserialized -> throw IllegalStateException("Property 'unit' is either missing or null.")
        }

        return Duration(
            value = value,
            unit = Duration.TimeUnit.valueOf(unit)
        )
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: Duration?) {
        requireNotNull(value) { "DurationAdapter is non-nullable, but received null." }

        writer.beginObject()
        writer.name("value").value(value.duration)
        writer.name("unit").value(SECONDS.toString())
        writer.endObject()
    }
}