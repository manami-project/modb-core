package io.github.manamiproject.modb.core.json

import com.squareup.moshi.JsonDataException
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.core.models.Duration
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import java.net.URI
import kotlin.io.path.inputStream
import kotlin.test.Test

internal class JsonKtTest {

    @Nested
    inner class DeserializationTests {

        @Test
        fun `deserialize Anime object using inputstream`() {
            runBlocking {
                // given
                val expectedAnime = Anime(
                    _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                    type = Anime.Type.TV,
                    episodes = 24,
                    status = Anime.Status.FINISHED,
                    animeSeason = AnimeSeason(
                        season = AnimeSeason.Season.SUMMER,
                        year = 2009,
                    ),
                    picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                    thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                    duration = Duration(24, Duration.TimeUnit.MINUTES),
                ).addSources(URI("https://myanimelist.net/anime/6351"))
                    .addRelatedAnime(URI("https://myanimelist.net/anime/2167"))
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
        }

        @Test
        fun `deserialize Anime object - all properties set`() {
            runBlocking {
                // given
                val expectedAnime = Anime(
                    _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                    type = Anime.Type.TV,
                    episodes = 24,
                    status = Anime.Status.FINISHED,
                    animeSeason = AnimeSeason(
                        season = AnimeSeason.Season.SUMMER,
                        year = 2009,
                    ),
                    picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                    thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                    duration = Duration(24, Duration.TimeUnit.MINUTES),
                ).addSources(URI("https://myanimelist.net/anime/6351"))
                    .addRelatedAnime(URI("https://myanimelist.net/anime/2167"))
                    .addSynonyms(
                        "Clannad ~After Story~: Another World, Kyou Chapter",
                        "Clannad: After Story OVA",
                        "クラナド　アフターストーリー　もうひとつの世界　杏編",
                    )
                    .addTags(
                        "comedy",
                        "romance",
                    )

                val json = loadTestResource<String>("json_tests/anime_all_properties_set.json")

                // when
                val result = Json.parseJson<Anime>(json)

                // then
                assertThat(result).isEqualTo(expectedAnime)
            }
        }

        @Test
        fun `deserialize Anime object - default properties`() {
            runBlocking {
                // given
                val expectedAnime = Anime("Death Note")

                val json = loadTestResource<String>("json_tests/anime_default_values.json")

                // when
                val result = Json.parseJson<Anime>(json)

                // then
                assertThat(result).isEqualTo(expectedAnime)
            }
        }

        @Test
        fun `deserialize Anime object - with duration missing`() {
            runBlocking {
                // given
                val expectedAnime = Anime("Death Note")

                val json = loadTestResource<String>("json_tests/anime_duration_missing.json")

                // when
                val result = Json.parseJson<Anime>(json)

                // then
                assertThat(result).isEqualTo(expectedAnime)
            }
        }

        @Test
        fun `deserialize object - throws exception if a property having a non-nullable type is mapped to null`() {
            // given
            val json = """
                {
                  "nullableString": null,
                  "nonNullableString": null
                }
            """.trimIndent()

            // when
            val result = exceptionExpected<JsonDataException> {
                Json.parseJson<NullableTestClass>(json)?.copy()
            }

            // then
            assertThat(result).hasMessage("Non-null value 'nonNullableString' was null at \$.nonNullableString")
        }

        @Test
        fun `deserialize an array - non nullable types with default value can contain null`() {
            runBlocking {
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
        }

        @Test
        fun `deserialize an array - Although the type of the list is non-nullable and copy is called on a list containing null, no exception is being thrown`() {
            runBlocking {
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
    }

    @Nested
    inner class SerializationTests {

        @Nested
        inner class DefaultOptionsTests {

            @Test
            fun `serialize anime - all properties set`() {
                runBlocking {
                    // given
                    val anime = Anime(
                        _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                        type = Anime.Type.TV,
                        episodes = 24,
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.SUMMER,
                            year = 2009,
                        ),
                        picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                        thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                        duration = Duration(24, Duration.TimeUnit.MINUTES)
                    ).addSources(
                        URI("https://livechart.me/anime/3681"),
                        URI("https://anisearch.com/anime/6826"),
                        URI("https://kitsu.io/anime/4529"),
                        URI("https://anime-planet.com/anime/clannad-another-world-kyou-chapter"),
                        URI("https://anilist.co/anime/6351"),
                        URI("https://notify.moe/anime/3L63cKimg"),
                        URI("https://myanimelist.net/anime/6351"),
                    ).addRelatedAnime(
                        URI("https://myanimelist.net/anime/4181"),
                        URI("https://anilist.co/anime/2167"),
                        URI("https://anime-planet.com/anime/clannad"),
                        URI("https://livechart.me/anime/10537"),
                        URI("https://anime-planet.com/anime/clannad-another-world-tomoyo-chapter"),
                        URI("https://livechart.me/anime/10976"),
                        URI("https://anime-planet.com/anime/clannad-movie"),
                        URI("https://anisearch.com/anime/4199"),
                        URI("https://notify.moe/anime/F2eY5Fmig"),
                        URI("https://livechart.me/anime/3581"),
                        URI("https://anime-planet.com/anime/clannad-after-story"),
                        URI("https://livechart.me/anime/3588"),
                        URI("https://myanimelist.net/anime/2167"),
                        URI("https://livechart.me/anime/3657"),
                        URI("https://anilist.co/anime/4059"),
                        URI("https://livechart.me/anime/3822"),
                        URI("https://kitsu.io/anime/1962"),
                    ).addSynonyms(
                        "Clannad (TV)",
                        "Kuranado",
                        "Clannad TV",
                        "CLANNAD",
                        "クラナド",
                        "Кланнад",
                        "Кланад",
                        "كلاناد",
                        "Clannad 1",
                        "클라나드",
                        "خانواده",
                        "کلاناد",
                        "แคลนนาด",
                        "くらなど",
                        "ＣＬＡＮＮＡＤ -クラナド-",
                    ).addTags(
                        "baseball",
                        "based on a visual novel",
                        "basketball",
                        "amnesia",
                        "coming of age",
                        "asia",
                        "daily life",
                        "comedy",
                        "delinquents",
                        "earth",
                        "romance",
                        "ensemble cast",
                        "drama",
                    )

                    val expectedJson = """
                        {
                          "title": "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                          "sources": [
                            "https://anilist.co/anime/6351",
                            "https://anime-planet.com/anime/clannad-another-world-kyou-chapter",
                            "https://anisearch.com/anime/6826",
                            "https://kitsu.io/anime/4529",
                            "https://livechart.me/anime/3681",
                            "https://myanimelist.net/anime/6351",
                            "https://notify.moe/anime/3L63cKimg"
                          ],
                          "synonyms": [
                            "CLANNAD",
                            "Clannad (TV)",
                            "Clannad 1",
                            "Clannad TV",
                            "Kuranado",
                            "Кланад",
                            "Кланнад",
                            "خانواده",
                            "كلاناد",
                            "کلاناد",
                            "แคลนนาด",
                            "くらなど",
                            "クラナド",
                            "클라나드",
                            "ＣＬＡＮＮＡＤ -クラナド-"
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
                            "value": 1440,
                            "unit": "SECONDS"
                          },
                          "relatedAnime": [
                            "https://anilist.co/anime/2167",
                            "https://anilist.co/anime/4059",
                            "https://anime-planet.com/anime/clannad",
                            "https://anime-planet.com/anime/clannad-after-story",
                            "https://anime-planet.com/anime/clannad-another-world-tomoyo-chapter",
                            "https://anime-planet.com/anime/clannad-movie",
                            "https://anisearch.com/anime/4199",
                            "https://kitsu.io/anime/1962",
                            "https://livechart.me/anime/10537",
                            "https://livechart.me/anime/10976",
                            "https://livechart.me/anime/3581",
                            "https://livechart.me/anime/3588",
                            "https://livechart.me/anime/3657",
                            "https://livechart.me/anime/3822",
                            "https://myanimelist.net/anime/2167",
                            "https://myanimelist.net/anime/4181",
                            "https://notify.moe/anime/F2eY5Fmig"
                          ],
                          "tags": [
                            "amnesia",
                            "asia",
                            "baseball",
                            "based on a visual novel",
                            "basketball",
                            "comedy",
                            "coming of age",
                            "daily life",
                            "delinquents",
                            "drama",
                            "earth",
                            "ensemble cast",
                            "romance"
                          ]
                        }
                    """.trimIndent()

                    // when
                    val result = Json.toJson(anime)

                    // then
                    assertThat(result).isEqualTo(expectedJson)
                }
            }

            @Test
            fun `serialize anime - default properties`() {
                runBlocking {
                    // given
                    val anime = Anime("Death Note")

                    val expectedJson = """
                        {
                          "title": "Death Note",
                          "sources": [],
                          "synonyms": [],
                          "type": "UNKNOWN",
                          "episodes": 0,
                          "status": "UNKNOWN",
                          "animeSeason": {
                            "season": "UNDEFINED",
                            "year": null
                          },
                          "picture": "https://raw.githubusercontent.com/manami-project/anime-offline-database/master/pics/no_pic.png",
                          "thumbnail": "https://raw.githubusercontent.com/manami-project/anime-offline-database/master/pics/no_pic_thumbnail.png",
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
            }

            @Test
            fun `serialize - option serialize null is activated by default`() {
                runBlocking {
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
        }

        @Nested
        inner class CustomOptionsTests {

            @Test
            fun `serialize - deactivate pretty print`() {
                runBlocking {
                    // given
                    val anime = Anime(
                        _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                        type = Anime.Type.TV,
                        episodes = 24,
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.SUMMER,
                            year = 2009,
                        ),
                        picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                        thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                        duration = Duration(24, Duration.TimeUnit.MINUTES),
                    ).addSources(URI("https://myanimelist.net/anime/6351"))
                        .addRelatedAnime(URI("https://myanimelist.net/anime/2167"))
                        .addSynonyms(
                            "Clannad ~After Story~: Another World, Kyou Chapter",
                            "Clannad: After Story OVA",
                            "クラナド　アフターストーリー　もうひとつの世界　杏編",
                        )
                        .addTags(
                            "comedy",
                            "romance",
                        )

                    val expectedJson = """{"title":"Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen","sources":["https://myanimelist.net/anime/6351"],"synonyms":["Clannad ~After Story~: Another World, Kyou Chapter","Clannad: After Story OVA","クラナド　アフターストーリー　もうひとつの世界　杏編"],"type":"TV","episodes":24,"status":"FINISHED","animeSeason":{"season":"SUMMER","year":2009},"picture":"https://cdn.myanimelist.net/images/anime/10/19621.jpg","thumbnail":"https://cdn.myanimelist.net/images/anime/10/19621t.jpg","duration":{"value":1440,"unit":"SECONDS"},"relatedAnime":["https://myanimelist.net/anime/2167"],"tags":["comedy","romance"]}""".trimIndent()

                    // when
                    val result = Json.toJson(anime, Json.SerializationOptions.DEACTIVATE_PRETTY_PRINT)

                    // then
                    assertThat(result).isEqualTo(expectedJson)
                }
            }

            @Test
            fun `serialize - deactivate serialize null`() {
                runBlocking {
                    // given
                    val expectedJson = """
                        {
                          "nonNullableString": "test"
                        }
                    """.trimIndent()

                    // when
                    val result = Json.toJson(NullableTestClass(), Json.SerializationOptions.DEACTIVATE_SERIALIZE_NULL)

                    // then
                    assertThat(result).isEqualTo(expectedJson)
                }
            }

            @Test
            fun `serialize - deactivate serialize Duration`() {
                runBlocking {
                    // given
                    val expectedJson = ""

                    // when
                    val result = Json.toJson(Duration.UNKNOWN, Json.SerializationOptions.DEACTIVATE_SERIALIZE_DURATION)

                    // then
                    assertThat(result).isEqualTo(expectedJson)
                }
            }
        }
    }
}

private data class NullableTestClass(val nullableString: String? = null, val nonNullableString: String = "test")
private data class ClassWithList(val nonNullableList: List<String> = emptyList())