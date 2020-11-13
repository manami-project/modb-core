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

        val expectedJson = "{\n" +
                "  \"_sources\": [\n" +
                "    \"https://myanimelist.net/anime/6351\"\n" +
                "  ],\n" +
                "  \"_synonyms\": [\n" +
                "    \"Clannad ~After Story~: Another World, Kyou Chapter\",\n" +
                "    \"Clannad: After Story OVA\",\n" +
                "    \"クラナド　アフターストーリー　もうひとつの世界　杏編\"\n" +
                "  ],\n" +
                "  \"_relatedAnime\": [\n" +
                "    \"https://myanimelist.net/anime/2167\"\n" +
                "  ],\n" +
                "  \"_tags\": [\n" +
                "    \"comedy\",\n"+
                "    \"romance\"\n"+
                "  ],\n" +
                "  \"_title\": \"Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen\",\n" +
                "  \"type\": \"TV\",\n" +
                "  \"episodes\": 24,\n" +
                "  \"status\": \"FINISHED\",\n" +
                "  \"animeSeason\": {\n" +
                "    \"season\": \"SUMMER\",\n" +
                "    \"_year\": 2009\n" +
                "  },\n" +
                "  \"picture\": \"https://cdn.myanimelist.net/images/anime/10/19621.jpg\",\n" +
                "  \"thumbnail\": \"https://cdn.myanimelist.net/images/anime/10/19621t.jpg\",\n" +
                "  \"duration\": {\n" +
                "    \"value\": 24,\n" +
                "    \"unit\": \"MINUTES\"\n" +
                "  }\n" +
                "}"

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

        val expectedJson = "{\n" +
                "  \"_sources\": [],\n" +
                "  \"_synonyms\": [],\n" +
                "  \"_relatedAnime\": [],\n" +
                "  \"_tags\": [],\n" +
                "  \"_title\": \"Death Note\",\n" +
                "  \"type\": \"TV\",\n" +
                "  \"episodes\": 0,\n" +
                "  \"status\": \"UNKNOWN\",\n" +
                "  \"animeSeason\": {\n" +
                "    \"season\": \"UNDEFINED\",\n" +
                "    \"_year\": 0\n" +
                "  },\n" +
                "  \"picture\": \"https://cdn.myanimelist.net/images/qm_50.gif\",\n" +
                "  \"thumbnail\": \"https://cdn.myanimelist.net/images/qm_50.gif\",\n" +
                "  \"duration\": {\n" +
                "    \"value\": 0,\n" +
                "    \"unit\": \"SECONDS\"\n" +
                "  }\n" +
                "}"

        // when
        val result = Json.toJson(anime)

        // then
        assertThat(result).isEqualTo(expectedJson)
    }
}