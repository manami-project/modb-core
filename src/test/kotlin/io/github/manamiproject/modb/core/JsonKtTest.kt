package io.github.manamiproject.modb.core

import io.github.manamiproject.modb.core.extensions.newInputStream
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
import org.junit.jupiter.api.Test
import java.net.URI

internal class JsonKtTest {

    @Test
    fun `deserialize anime using input stream`() {
        // given
        val expectedAnime =  Anime(
            _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
            type = TV,
            episodes = 24,
            status = FINISHED,
            animeSeason = AnimeSeason(
                season = SUMMER,
                _year = 2009
            ),
            picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
            thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
            duration = Duration(24, MINUTES)
        ).addSources(
                mutableListOf(
                        URI("https://myanimelist.net/anime/6351")
                )
        ).addSynonyms(
                mutableListOf(
                        "Clannad ~After Story~: Another World, Kyou Chapter",
                        "Clannad: After Story OVA",
                        "クラナド　アフターストーリー　もうひとつの世界　杏編"
                )
        ).addRelations(
                mutableListOf(
                        URI("https://myanimelist.net/anime/2167")
                )
        ).addTags(
                mutableListOf(
                        "comedy",
                        "romance"
                )
        )



        val inputStream = testResource("json_tests/anime_all_properties_set.json").newInputStream()

        // when
        val result = Json.parseJson<Anime>(inputStream)

        // then
        assertThat(result).isEqualTo(expectedAnime)
    }

    @Test
    fun `deserialize anime - all properties set`() {
        // given
        val expectedAnime = Anime(
            _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
            type = TV,
            episodes = 24,
            status = FINISHED,
            animeSeason = AnimeSeason(
                season = SUMMER,
                _year = 2009
            ),
            picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
            thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
            duration = Duration(24, MINUTES)
        ).addSources(
                mutableListOf(
                        URI("https://myanimelist.net/anime/6351")
                )
        ).addSynonyms(
                mutableListOf(
                        "Clannad ~After Story~: Another World, Kyou Chapter",
                        "Clannad: After Story OVA",
                        "クラナド　アフターストーリー　もうひとつの世界　杏編"
                )
        ).addRelations(
                mutableListOf(
                        URI("https://myanimelist.net/anime/2167")
                )
        ).addTags(
                mutableListOf(
                        "comedy",
                        "romance"
                )
        )

        val json = loadTestResource("json_tests/anime_all_properties_set.json")

        // when
        val result = Json.parseJson<Anime>(json)

        // then
        assertThat(result).isEqualTo(expectedAnime)
    }

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
                _year = 2009
            ),
            picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
            thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
            duration = Duration(24, MINUTES)
        ).addSynonyms(
                mutableListOf(
                        "Clannad ~After Story~: Another World, Kyou Chapter",
                        "Clannad: After Story OVA",
                        "クラナド　アフターストーリー　もうひとつの世界　杏編"
                )
        ).addSources(
                mutableListOf(
                        URI("https://myanimelist.net/anime/6351")
                )
        ).addRelations(
                mutableListOf(
                        URI("https://myanimelist.net/anime/2167")
                )
        ).addTags(
                mutableListOf(
                        "comedy",
                        "romance"
                )
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
                "_year": 2009
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
    fun `deserialize anime - default properties`() {
        // given
        val expectedAnime = Anime("Death Note")

        val json = loadTestResource("json_tests/anime_default_values.json")

        // when
        val result = Json.parseJson<Anime>(json)

        // then
        assertThat(result).isEqualTo(expectedAnime)
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
                "_year": 0
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
}