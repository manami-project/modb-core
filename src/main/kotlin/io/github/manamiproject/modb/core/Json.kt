package io.github.manamiproject.modb.core

import com.beust.klaxon.*
import com.google.gson.GsonBuilder
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status
import io.github.manamiproject.modb.core.models.Anime.Type
import io.github.manamiproject.modb.core.models.AnimeSeason.Season
import io.github.manamiproject.modb.core.models.Duration.TimeUnit as DurationUnit
import java.io.InputStream
import java.net.URL

/**
 * Handles serialization and deserialization of objects to/from JSON.
 * @since 1.0.0
 */
object Json {

    private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

    /**
     * Parse a [String] into an object.
     * @since 1.0.0
     * @param json Valid JSON as [String]
     * @return Deserialzed JSON as object of given type [T]
     */
    inline fun <reified T> parseJson(json: String): T? {
        return Klaxon()
                .converter(AnimeKlaxonConverter())
                .parse<T>(json)
    }

    /**
     * Parse an [InputStream] into an object.
     * @since 1.0.0
     * @param json Valid JSON as [InputStream]
     * @return Deserialzed JSON as object of given type [T]
     */
    inline fun <reified T> parseJson(json: InputStream): T? {
        return Klaxon()
                .converter(AnimeKlaxonConverter())
                .parse<T>(json)
    }

    /**
     * Serialize any object to JSON.
     * @since 1.0.0
     * @param obj Any object that is supposed to be serialized to JSON.
     * @return Given object serialized in JSON as [String]
     */
    fun toJson(obj: Any): String = gson.toJson(obj)
}


@PublishedApi
internal class AnimeKlaxonConverter : Converter {

    override fun canConvert(cls: Class<*>): Boolean = cls == Anime::class.java

    override fun toJson(value: Any): String = throw IllegalStateException("Use io.github.manamiproject.modb.Json.toJson() instead")

    override fun fromJson(jv: JsonValue): Anime {
        return Anime(
            _title = jv.objString("_title"),
            episodes = jv.objInt("episodes"),
            type = Type.valueOf(jv.objString("type")),
            status = Status.valueOf(jv.objString("status")),
            animeSeason = AnimeSeason(
                _year = (jv.obj?.get("animeSeason") as JsonObject).int("_year") ?: 0,
                season = Season.of((jv.obj?.get("animeSeason") as JsonObject).string("season")!!)
            ),
            thumbnail = URL(jv.objString("thumbnail")),
            picture = URL(jv.objString("picture")),
            duration = Duration(
                value = (jv.obj?.get("duration") as JsonObject).int("value")!!,
                unit = DurationUnit.valueOf((jv.obj?.get("duration") as JsonObject).string("unit")!!)
            ),
            _synonyms = (jv.obj?.get("_synonyms") as JsonArray<*>).value.map { it as String }.toMutableList(),
            _sources = (jv.obj?.get("_sources") as JsonArray<*>).value.map { it as String }.map { URL(it) }.toMutableList(),
            _relatedAnime = (jv.obj?.get("_relatedAnime") as JsonArray<*>).value.map { it as String }.map { URL(it) }.toMutableList(),
            _tags =(jv.obj?.get("_tags") as JsonArray<*>).value.map { it as String }.toMutableList()
        )
    }
}