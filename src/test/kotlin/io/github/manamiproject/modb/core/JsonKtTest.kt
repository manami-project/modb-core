package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_PRETTY_PRINT
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_SERIALIZE_NULL
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.Anime.Status.FINISHED
import io.github.manamiproject.modb.core.models.Anime.Type.TV
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.SUMMER
import io.github.manamiproject.modb.core.models.Duration
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.testResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI
import kotlin.io.path.inputStream

internal class JsonKtTest {

    @Nested
    inner class DeserializationTests {

        @Test
        fun `deserialize Anime object using inputstream`() {
            // given
            val expectedAnime =  Anime(
                _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                type = TV,
                episodes = 24,
                status = FINISHED,
                animeSeason = AnimeSeason(
                    season = SUMMER,
                    year = 2009
                ),
                picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                duration = Duration(24, MINUTES)
            ).addSources(URI("https://myanimelist.net/anime/6351"))
                .addRelations(URI("https://myanimelist.net/anime/2167"))
                .addSynonyms(
                    "Clannad ~After Story~: Another World, Kyou Chapter",
                    "Clannad: After Story OVA",
                    "クラナド　アフターストーリー　もうひとつの世界　杏編",
                ).addTags(
                    "comedy",
                    "romance",
                )

            val inputStream = testResource("json_tests/anime_all_properties_set.json").inputStream()

            // when
            val result = Json.parseJson<Anime>(inputStream)

            // then
            assertThat(result).isEqualTo(expectedAnime)
        }

        @Test
        fun `deserialize Anime object - all properties set`() {
            // given
            val expectedAnime = Anime(
                _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                type = TV,
                episodes = 24,
                status = FINISHED,
                animeSeason = AnimeSeason(
                    season = SUMMER,
                    year = 2009
                ),
                picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                duration = Duration(24, MINUTES)
            ).addSources(URI("https://myanimelist.net/anime/6351"))
                .addRelations(URI("https://myanimelist.net/anime/2167"))
                .addSynonyms(
                    "Clannad ~After Story~: Another World, Kyou Chapter",
                    "Clannad: After Story OVA",
                    "クラナド　アフターストーリー　もうひとつの世界　杏編",
                )
                .addTags(
                    "comedy",
                    "romance",
                )

            val json = loadTestResource("json_tests/anime_all_properties_set.json")

            // when
            val result = Json.parseJson<Anime>(json)

            // then
            assertThat(result).isEqualTo(expectedAnime)
        }

        @Test
        fun `deserialize Anime object - default properties`() {
            // given
            val expectedAnime = Anime("Death Note")

            val json = loadTestResource("json_tests/anime_default_values.json")

            // when
            val result = Json.parseJson<Anime>(json)

            // then
            assertThat(result).isEqualTo(expectedAnime)
        }

        @Test
        fun `deserialize object - non nullable types with default value can contain null`() {
            // given
            val json = """
                {
                  "nullableString": null,
                  "nonNullableString": null
                }
            """.trimIndent()

            // when
            val result = Json.parseJson<NullableTestClass>(json)

            // then
            assertThat(result?.nonNullableString).isNull()
            assertThat(result?.nullableString).isNull()
        }

        @Test
        fun `deserialize object - throws NullPointerException if copy() is called on an object having a property of non-nullable type, but containing null as value`() {
            // given
            val json = """
                {
                  "nullableString": null,
                  "nonNullableString": null
                }
            """.trimIndent()

            // when
            val result = assertThrows<NullPointerException> {
                Json.parseJson<NullableTestClass>(json)?.copy()
            }

            // then
            assertThat(result).hasMessage("Parameter specified as non-null is null: method io.github.manamiproject.modb.core.NullableTestClass.copy, parameter nonNullableString")
        }

        @Test
        fun `deserialize an array - non nullable types with default value can contain null`() {
            // given
            val json = """
                {
                  "nonNullableList": [
                    "value1",
                    null,
                    "value3"
                  ]
                }
            """.trimIndent()

            // when
            val result = Json.parseJson<ClassWithList>(json)

            // then
            assertThat(result?.nonNullableList).containsNull()
        }

        @Test
        fun `deserialize an arry - Although the type of the list is non-nullable and copy is called on a list containing null, no exception is being thrown`() {
            // given
            val json = """
                {
                  "nonNullableList": [
                    "value1",
                    null,
                    "value3"
                  ]
                }
            """.trimIndent()

            // when
            val result = Json.parseJson<ClassWithList>(json)?.copy()!!

            // then
            assertThat(result.nonNullableList).containsNull()
        }
    }

    @Nested
    inner class SerializationTests {

        @Nested
        inner class DefaultOptionsTests {

            @Test
            fun `serialize anime - all properties set`() {
                // given
                val anime = Anime(
                    _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                    type = TV,
                    episodes = 24,
                    status = FINISHED,
                    animeSeason = AnimeSeason(
                        season = SUMMER,
                        year = 2009
                    ),
                    picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                    thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                    duration = Duration(24, MINUTES)
                ).addSources(URI("https://myanimelist.net/anime/6351"))
                    .addRelations(URI("https://myanimelist.net/anime/2167"))
                    .addSynonyms(
                        "Clannad ~After Story~: Another World, Kyou Chapter",
                        "Clannad: After Story OVA",
                        "クラナド　アフターストーリー　もうひとつの世界　杏編",
                    )
                    .addTags(
                        "comedy",
                        "romance",
                    )

                val expectedJson = """
        {
          "_title": "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
          "sources": [
            "https://myanimelist.net/anime/6351"
          ],
          "synonyms": [
            "Clannad ~After Story~: Another World, Kyou Chapter",
            "Clannad: After Story OVA",
            "クラナド　アフターストーリー　もうひとつの世界　杏編"
          ],
          "type": "TV",
          "episodes": 24,
          "status": "FINISHED",
          "animeSeason": {
            "season": "SUMMER",
            "year": 2009
          },
          "picture": "https://cdn.myanimelist.net/images/anime/10/19621.jpg",
          "thumbnail": "https://cdn.myanimelist.net/images/anime/10/19621t.jpg",
          "duration": {
            "value": 24,
            "unit": "MINUTES"
          },
          "relatedAnime": [
            "https://myanimelist.net/anime/2167"
          ],
          "tags": [
            "comedy",
            "romance"
          ]
        }
    """.trimIndent()

                // when
                val result = Json.toJson(anime)

                // then
                assertThat(result).isEqualTo(expectedJson)
            }

            @Test
            fun `serialize anime - default properties`() {
                // given
                val anime = Anime("Death Note")

                val expectedJson = """
        {
          "_title": "Death Note",
          "sources": [],
          "synonyms": [],
          "type": "TV",
          "episodes": 0,
          "status": "UNKNOWN",
          "animeSeason": {
            "season": "UNDEFINED",
            "year": 0
          },
          "picture": "https://cdn.myanimelist.net/images/qm_50.gif",
          "thumbnail": "https://cdn.myanimelist.net/images/qm_50.gif",
          "duration": {
            "value": 0,
            "unit": "SECONDS"
          },
          "relatedAnime": [],
          "tags": []
        }
    """.trimIndent()

                // when
                val result = Json.toJson(anime)

                // then
                assertThat(result).isEqualTo(expectedJson)
            }

            @Test
            fun `serialize - option serialize null is activated by default`() {
                // given

                val expectedJson = """
        {
          "nullableString": null,
          "nonNullableString": "test"
        }
    """.trimIndent()

                // when
                val result = Json.toJson(NullableTestClass())

                // then
                assertThat(result).isEqualTo(expectedJson)
            }
        }

        @Nested
        inner class CustomOptionsTests {

            @Test
            fun `serialize - deactivate pretty print`() {
                // given
                val anime = Anime(
                    _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                    type = TV,
                    episodes = 24,
                    status = FINISHED,
                    animeSeason = AnimeSeason(
                        season = SUMMER,
                        year = 2009
                    ),
                    picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                    thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                    duration = Duration(24, MINUTES)
                ).addSources(URI("https://myanimelist.net/anime/6351"))
                    .addRelations(URI("https://myanimelist.net/anime/2167"))
                    .addSynonyms(
                        "Clannad ~After Story~: Another World, Kyou Chapter",
                        "Clannad: After Story OVA",
                        "クラナド　アフターストーリー　もうひとつの世界　杏編",
                    )
                    .addTags(
                        "comedy",
                        "romance",
                    )

                val expectedJson = """{"_title":"Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen","sources":["https://myanimelist.net/anime/6351"],"synonyms":["Clannad ~After Story~: Another World, Kyou Chapter","Clannad: After Story OVA","クラナド　アフターストーリー　もうひとつの世界　杏編"],"type":"TV","episodes":24,"status":"FINISHED","animeSeason":{"season":"SUMMER","year":2009},"picture":"https://cdn.myanimelist.net/images/anime/10/19621.jpg","thumbnail":"https://cdn.myanimelist.net/images/anime/10/19621t.jpg","duration":{"value":24,"unit":"MINUTES"},"relatedAnime":["https://myanimelist.net/anime/2167"],"tags":["comedy","romance"]}""".trimIndent()

                // when
                val result = Json.toJson(anime, DEACTIVATE_PRETTY_PRINT)

                // then
                assertThat(result).isEqualTo(expectedJson)
            }

            @Test
            fun `serialize - deactivate serialize null`() {
                // given

                val expectedJson = """
        {
          "nonNullableString": "test"
        }
    """.trimIndent()

                // when
                val result = Json.toJson(NullableTestClass(), DEACTIVATE_SERIALIZE_NULL)

                // then
                assertThat(result).isEqualTo(expectedJson)
            }
        }
    }
}

private data class NullableTestClass(val nullableString: String? = null, val nonNullableString: String = "test")
private data class ClassWithList(val nonNullableList: List<String> = emptyList())