package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.collections.SortedList.Companion.STRING_COMPARATOR
import io.github.manamiproject.modb.core.collections.SortedList.Companion.URI_COMPARATOR
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.models.Anime.Status.FINISHED
import io.github.manamiproject.modb.core.models.Anime.Type.Special
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI

internal class AnimeKtTest {

    @Nested
    inner class TitleTests {

        @Test
        fun `remove leading whitespace from title`() {
            // given
            val expectedTitle = "Death Note"

            // when
            val result = Anime(" $expectedTitle")

            // then
            assertThat(result.title).isEqualTo(expectedTitle)
        }

        @Test
        fun `remove tailing whitespace from title`() {
            // given
            val expectedTitle = "Death Note"

            // when
            val result = Anime("$expectedTitle ")

            // then
            assertThat(result.title).isEqualTo(expectedTitle)
        }

        @Test
        fun `replace multiple whitespaces with a single whitespace in title`() {
            // given
            val expectedTitle = "Death Note"

            // when
            val result = Anime("Death    Note")

            // then
            assertThat(result.title).isEqualTo(expectedTitle)
        }

        @Test
        fun `replace tab character with whitespace in title`() {
            // given
            val expectedTitle = "Death Note"

            // when
            val result = Anime("Death\tNote")

            // then
            assertThat(result.title).isEqualTo(expectedTitle)
        }

        @Test
        fun `replace line feed character with whitespace in title`() {
            // given
            val expectedTitle = "Death Note"

            // when
            val result = Anime("Death\nNote")

            // then
            assertThat(result.title).isEqualTo(expectedTitle)
        }

        @Test
        fun `replace carriage return line feed with whitespace in title`() {
            // given
            val expectedTitle = "Death Note"

            // when
            val result = Anime("Death\r\nNote")

            // then
            assertThat(result.title).isEqualTo(expectedTitle)
        }

        @Test
        fun `throw exception if title is empty`() {
            // when
            val result = assertThrows<IllegalArgumentException> {
                Anime(EMPTY)
            }

            // then
            assertThat(result).hasMessage("Title cannot be blank.")
        }

        @Test
        fun `throw exception if title is blank`() {
            // when
            val result = assertThrows<IllegalArgumentException> {
                Anime("     ")
            }

            // then
            assertThat(result).hasMessage("Title cannot be blank.")
        }
    }

    @Nested
    inner class SynonymsTests {

        @Nested
        inner class AddSynonymsConstructorTests {

            @Test
            fun `must not add a synonym if it equals the title`() {
                // given
                val title = "Death Note"

                // when
                val result = Anime(
                    _title =  title,
                    synonyms = SortedList(mutableListOf(title), STRING_COMPARATOR),
                )

                // then
                assertThat(result.synonyms).isEmpty()
            }

            @Test
            fun `must not add blank synonym`() {
                // when
                val result = Anime(
                    _title = "Death Note",
                    synonyms = SortedList(mutableListOf("         "), STRING_COMPARATOR),
                )

                // then
                assertThat(result.synonyms).isEmpty()
            }

            @Test
            fun `successfully add a synonym`() {
                // given
                val synonym = "Caderno da Morte"

                // when
                val result = Anime(
                    _title = "Death Note",
                    synonyms = SortedList(mutableListOf(synonym), STRING_COMPARATOR),
                )

                // then
                assertThat(result.synonyms).containsExactly(synonym)
            }

            @Test
            fun `must not add a duplicated synonym`() {
                // given
                val synonym = "Caderno da Morte"

                // when
                val result = Anime(
                    _title = "Death Note",
                    synonyms = SortedList(mutableListOf(synonym, synonym), STRING_COMPARATOR),
                )

                // then
                assertThat(result.synonyms).containsExactly(synonym)
            }

            @Test
            fun `list of synonyms is sorted ascending`() {
                // given
                val one = "Caderno da Morte"
                val two =  "DN"
                val three =  "Quaderno della Morte"
                val four = "Sveska Smrti"

                // when
                val result = Anime(
                    _title = "Death Note",
                    synonyms = SortedList(
                        list = mutableListOf(
                            four,
                            two,
                            three,
                            one
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(one, two, three, four)
            }

            @Test
            fun `synonym comparison to title is not case sensitive`() {
                // given
                val title  =  "Death Note"

                // when
                val result = Anime(
                    _title = title,
                    synonyms = SortedList(mutableListOf(title.toUpperCase()), STRING_COMPARATOR),
                )

                // then
                assertThat(result.synonyms).containsExactly(title.toUpperCase())
            }

            @Test
            fun `synonym comparison is not case sensitive`() {
                // given
                val title  =  "Death Note"

                // when
                val result = Anime(
                    _title = title,
                    synonyms = SortedList(
                        list = mutableListOf(
                            title.toLowerCase(),
                            title.toUpperCase()
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(title.toUpperCase(), title.toLowerCase())
            }

            @Test
            fun `remove leading whitespace from synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"

                // when
                val result = Anime(
                    _title = "Title",
                    synonyms = SortedList(
                        list = mutableListOf(
                            " $expectedTitleOne",
                            " $expectedTitleTwo"
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `remove tailing whitespace from synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"

                // when
                val result = Anime(
                    _title = "Title",
                    synonyms = SortedList(
                        list = mutableListOf(
                            "$expectedTitleOne ",
                            "$expectedTitleTwo "
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace multiple whitespaces with a single whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"

                // when
                val result = Anime(
                    _title = "Title",
                    synonyms = SortedList(
                        list = mutableListOf(
                            "Death        Note",
                            "Made      in        Abyss"
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace tab character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"

                // when
                val result = Anime(
                    _title = "Title",
                    synonyms = SortedList(
                        list = mutableListOf(
                            "Death\tNote",
                            "Made\tin\tAbyss"
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace line feed character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"

                // when
                val result = Anime(
                    _title = "Title",
                    synonyms = SortedList(
                        list = mutableListOf(
                            "Death\nNote",
                            "Made\nin\nAbyss"
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace carriage return line feed character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"

                // when
                val result = Anime(
                    _title = "Title",
                    synonyms = SortedList(
                        list = mutableListOf(
                            "Death\r\nNote",
                            "Made\r\nin\r\nAbyss"
                        ),
                        comparator= STRING_COMPARATOR
                    ),
                )

                // then
                assertThat(result.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }
        }

        @Nested
        inner class AddSynonymsTests {

            @Test
            fun `must not add a synonym if it equals the title`() {
                // given
                val anime = Anime("Death Note")

                // when
                anime.addSynonyms(listOf("Death Note"))

                // then
                assertThat(anime.synonyms).isEmpty()
            }

            @Test
            fun `must not add blank synonym`() {
                // given
                val anime = Anime("Death Note")

                // when
                anime.addSynonyms(listOf("         "))

                // then
                assertThat(anime.synonyms).isEmpty()
            }

            @Test
            fun `successfully add a synonym`() {
                // given
                val one = "Caderno da Morte"
                val two = "DN"
                val three = "Quaderno della Morte"
                val four = "Sveska Smrti"

                val anime = Anime("Death Note")

                // when
                anime.addSynonyms(listOf(four, two, three, one))

                // then
                assertThat(anime.synonyms).containsExactly(one, two, three, four)
            }

            @Test
            fun `must not add a duplicated synonym`() {
                // given
                val one = "Caderno da Morte"
                val two = "DN"

                val anime = Anime(
                    _title = "Death Note"
                ).addSynonyms(listOf(two))

                // when
                anime.addSynonyms(listOf(two, one))

                // then
                assertThat(anime.synonyms).containsExactly(one, two)
            }

            @Test
            fun `list of synonyms is sorted ascending`() {
                // given
                val one = "Caderno da Morte"
                val two =  "DN"
                val three =  "Quaderno della Morte"
                val four = "Sveska Smrti"

                val anime = Anime("Death Note")

                // when
                anime.addSynonyms(
                    listOf(
                        four,
                        two,
                        three,
                        one
                    )
                )

                // then
                assertThat(anime.synonyms).containsExactly(one, two, three, four)
            }

            @Test
            fun `synonym comparison to title is not case sensitive`() {
                // given
                val title  =  "Death Note"
                val anime = Anime(title)

                // when
                anime.addSynonyms(listOf(title.toUpperCase()))

                // then
                assertThat(anime.synonyms).containsExactly(title.toUpperCase())
            }

            @Test
            fun `synonym comparison is not case sensitive`() {
                // given
                val title  =  "Death Note"
                val anime = Anime(title)

                // when
                anime.addSynonyms(listOf(title.toLowerCase(), title.toUpperCase()))

                // then
                assertThat(anime.synonyms).containsExactly(title.toUpperCase(), title.toLowerCase())
            }

            @Test
            fun `remove leading whitespace from synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(listOf(" $expectedTitleOne", " $expectedTitleTwo"))

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `remove tailing whitespace from synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(listOf("$expectedTitleOne ", "$expectedTitleTwo "))

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace multiple whitespaces with a single whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(listOf("Death        Note", "Made      in        Abyss"))

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace tab character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(listOf("Death\tNote", "Made\tin\tAbyss"))

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace line feed character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(listOf("Death\nNote", "Made\nin\nAbyss"))

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace carriage return line feed character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(listOf("Death\r\nNote", "Made\r\nin\r\nAbyss"))

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }
        }

        @Nested
        inner class AddSynonymsVarargTests {

            @Test
            fun `must not add a synonym if it equals the title`() {
                // given
                val anime = Anime("Death Note")

                // when
                anime.addSynonyms("Death Note")

                // then
                assertThat(anime.synonyms).isEmpty()
            }

            @Test
            fun `must not add blank synonym`() {
                // given
                val anime = Anime("Death Note")

                // when
                anime.addSynonyms("         ")

                // then
                assertThat(anime.synonyms).isEmpty()
            }

            @Test
            fun `successfully add a synonym`() {
                // given
                val one = "Caderno da Morte"
                val two = "DN"
                val three = "Quaderno della Morte"
                val four = "Sveska Smrti"

                val anime = Anime("Death Note")

                // when
                anime.addSynonyms(four, two, three, one)

                // then
                assertThat(anime.synonyms).containsExactly(one, two, three, four)
            }

            @Test
            fun `must not add a duplicated synonym`() {
                // given
                val one = "Caderno da Morte"
                val two = "DN"

                val anime = Anime(
                    _title = "Death Note"
                ).addSynonyms(two)

                // when
                anime.addSynonyms(two, one)

                // then
                assertThat(anime.synonyms).containsExactly(one, two)
            }

            @Test
            fun `list of synonyms is sorted ascending`() {
                // given
                val one = "Caderno da Morte"
                val two =  "DN"
                val three =  "Quaderno della Morte"
                val four = "Sveska Smrti"

                val anime = Anime("Death Note")

                // when
                anime.addSynonyms(four, two, three, one)

                // then
                assertThat(anime.synonyms).containsExactly(one, two, three, four)
            }

            @Test
            fun `synonym comparison to title is not case sensitive`() {
                // given
                val title  =  "Death Note"
                val anime = Anime(title)

                // when
                anime.addSynonyms(title.toUpperCase())

                // then
                assertThat(anime.synonyms).containsExactly(title.toUpperCase())
            }

            @Test
            fun `synonym comparison is not case sensitive`() {
                // given
                val title  =  "Death Note"
                val anime = Anime(title)

                // when
                anime.addSynonyms(title.toLowerCase(), title.toUpperCase())

                // then
                assertThat(anime.synonyms).containsExactly(title.toUpperCase(), title.toLowerCase())
            }

            @Test
            fun `remove leading whitespace from synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms(" $expectedTitleOne", " $expectedTitleTwo")

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `remove tailing whitespace from synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms("$expectedTitleOne ", "$expectedTitleTwo ")

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace multiple whitespaces with a single whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms("Death        Note", "Made      in        Abyss")

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace tab character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms("Death\tNote", "Made\tin\tAbyss")

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace line feed character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms("Death\nNote", "Made\nin\nAbyss")

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }

            @Test
            fun `replace carriage return line feed character with whitespace in synonyms`() {
                // given
                val expectedTitleOne = "Death Note"
                val expectedTitleTwo = "Made in Abyss"
                val anime = Anime("Title")

                // when
                anime.addSynonyms("Death\r\nNote", "Made\r\nin\r\nAbyss")

                // then
                assertThat(anime.synonyms).containsExactly(expectedTitleOne, expectedTitleTwo)
            }
        }
    }

    @Nested
    inner class SourcesTests {

        @Nested
        inner class AddSourcesConstructorTests {

            @Test
            fun `add source`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")

                // when
                val anime = Anime(
                    _title =  "Death Note",
                    sources = SortedList(mutableListOf(source), URI_COMPARATOR)
                )

                // then
                assertThat(anime.sources).containsExactly(source)
            }

            @Test
            fun `cannot add duplicated source link`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")

                // when
                val result = Anime(
                    _title =  "Death Note",
                    sources = SortedList(mutableListOf(source, source), URI_COMPARATOR),
                )

                // then
                assertThat(result.sources).containsExactly(source)
            }

            @Test
            fun `list of source links is sorted ascending`() {
                // given
                val four = URI("https://myanimelist.net/anime/1535")
                val two = URI("https://anilist.co/anime/1535")
                val three = URI("https://kitsu.io/anime/1376")
                val one = URI("https://anidb.net/anime/4563")

                // when
                val result = Anime(
                    _title = "Death Note",
                    sources = SortedList(mutableListOf(four, two, three, one), URI_COMPARATOR),
                )

                // then
                assertThat(result.sources).containsExactly(one, two, three, four)
            }

            @Test
            fun `remove related anime if the same uri has been added to sources`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")

                // when
                val result = Anime(
                    _title =  "Death Note",
                    relatedAnime = SortedList(mutableListOf(source), URI_COMPARATOR),
                    sources = SortedList(mutableListOf(source), URI_COMPARATOR),
                )

                // then
                assertThat(result.sources).containsExactly(source)
                assertThat(result.relatedAnime).isEmpty()
            }
        }

        @Nested
        inner class AddSourcesTests {

            @Test
            fun `add source`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                )

                // when
                anime.addSources(listOf(source))

                // then
                assertThat(anime.sources).containsExactly(source)
            }

            @Test
            fun `cannot add duplicated source link`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                ).addSources(listOf(source))

                // when
                anime.addSources(listOf(source))

                // then
                assertThat(anime.sources).containsExactly(source)
            }

            @Test
            fun `list of source links is sorted ascending`() {
                // given
                val anime = Anime("Death Note")

                val four = URI("https://myanimelist.net/anime/1535")
                val two = URI("https://anilist.co/anime/1535")
                val three = URI("https://kitsu.io/anime/1376")
                val one = URI("https://anidb.net/anime/4563")

                // when
                anime.addSources(listOf(four, two, three, one))

                // then
                assertThat(anime.sources).containsExactly(one, two, three, four)
            }

            @Test
            fun `remove related anime if the same uri has been added to sources`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                        _title =  "Death Note"
                ).addRelations(listOf(source))

                // when
                anime.addSources(listOf(source))

                // then
                assertThat(anime.sources).containsExactly(source)
                assertThat(anime.relatedAnime).isEmpty()
            }
        }

        @Nested
        inner class AddSourcesVarargTests {

            @Test
            fun `add source`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                )

                // when
                anime.addSources(source)

                // then
                assertThat(anime.sources).containsExactly(source)
            }

            @Test
            fun `cannot add duplicated source link`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                ).addSources(source)

                // when
                anime.addSources(source)

                // then
                assertThat(anime.sources).containsExactly(source)
            }

            @Test
            fun `list of source links is sorted ascending`() {
                // given
                val anime = Anime("Death Note")

                val four = URI("https://myanimelist.net/anime/1535")
                val two = URI("https://anilist.co/anime/1535")
                val three = URI("https://kitsu.io/anime/1376")
                val one = URI("https://anidb.net/anime/4563")

                // when
                anime.addSources(four, two, three, one)

                // then
                assertThat(anime.sources).containsExactly(one, two, three, four)
            }

            @Test
            fun `remove related anime if the same uri has been added to sources`() {
                // given
                val source = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                ).addRelations(source)

                // when
                anime.addSources(source)

                // then
                assertThat(anime.sources).containsExactly(source)
                assertThat(anime.relatedAnime).isEmpty()
            }
        }
    }

    @Nested
    inner class RelatedAnimeTests {

        @Nested
        inner class AddRelatedAnimeConstructorTests {

            @Test
            fun `add related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")

                // when
                val result = Anime(
                    _title =  "Death Note",
                    relatedAnime = SortedList(mutableListOf(relatedAnime), URI_COMPARATOR),
                )

                // then
                assertThat(result.relatedAnime).containsExactly(relatedAnime)
            }

            @Test
            fun `cannot add duplicated link for related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")

                // when
                val result = Anime(
                    _title =  "Death Note",
                    relatedAnime = SortedList(mutableListOf(relatedAnime, relatedAnime), URI_COMPARATOR),
                )

                // then
                assertThat(result.relatedAnime).containsExactly(relatedAnime)
            }

            @Test
            fun `list of related anime is sorted ascending`() {
                // given
                val four = URI("https://myanimelist.net/anime/2994")
                val two = URI("https://anidb.net/anime/8146")
                val three = URI("https://anidb.net/anime/8147")
                val one = URI("http://anilist.co/anime/2994")

                // when
                val result = Anime(
                    _title =  "Death Note",
                    relatedAnime = SortedList(mutableListOf(four, two, three, one), URI_COMPARATOR),
                )

                // then
                assertThat(result.relatedAnime).containsExactly(one, two, three, four)
            }

            @Test
            fun `cannot add a related anime if the links is already part of the sources`() {
                // given
                val link = URI("https://myanimelist.net/anime/1535")

                // when
                val result = Anime(
                    _title =  "Death Note",
                    sources = SortedList(mutableListOf(link), URI_COMPARATOR),
                    relatedAnime = SortedList(mutableListOf(link), URI_COMPARATOR),
                )

                // then
                assertThat(result.relatedAnime).isEmpty()
            }
        }

        @Nested
        inner class AddRelatedAnimeTests {

            @Test
            fun `add related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")
                val anime = Anime(
                    _title =  "Death Note"
                )

                // when
                anime.addRelations(listOf(relatedAnime))

                // then
                assertThat(anime.relatedAnime).containsExactly(relatedAnime)
            }

            @Test
            fun `cannot add duplicated link for related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")
                val anime = Anime(
                    _title =  "Death Note"
                ).addRelations(listOf(relatedAnime))

                // when
                anime.addRelations(listOf(relatedAnime))

                // then
                assertThat(anime.relatedAnime).containsExactly(relatedAnime)
            }

            @Test
            fun `list of related anime is sorted ascending`() {
                // given
                val anime = Anime("Death Note")

                val four = URI("https://myanimelist.net/anime/2994")
                val two = URI("https://anidb.net/anime/8146")
                val three = URI("https://anidb.net/anime/8147")
                val one = URI("http://anilist.co/anime/2994")

                // when
                anime.addRelations(listOf(four, two, three, one))

                // then
                assertThat(anime.relatedAnime).containsExactly(one, two, three, four)
            }

            @Test
            fun `cannot add a related anime if the links is already part of the sources`() {
                // given
                val link = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                ).addSources(listOf(link))

                // when
                anime.addRelations(listOf(link))

                // then
                assertThat(anime.relatedAnime).isEmpty()
            }
        }

        @Nested
        inner class AddRelatedAnimeVarargTests {

            @Test
            fun `add related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")
                val anime = Anime(
                    _title =  "Death Note"
                )

                // when
                anime.addRelations(relatedAnime)

                // then
                assertThat(anime.relatedAnime).containsExactly(relatedAnime)
            }

            @Test
            fun `cannot add duplicated link for related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")
                val anime = Anime(
                    _title =  "Death Note"
                ).addRelations(relatedAnime)

                // when
                anime.addRelations(relatedAnime)

                // then
                assertThat(anime.relatedAnime).containsExactly(relatedAnime)
            }

            @Test
            fun `list of related anime is sorted ascending`() {
                // given
                val anime = Anime("Death Note")

                val four = URI("https://myanimelist.net/anime/2994")
                val two = URI("https://anidb.net/anime/8146")
                val three = URI("https://anidb.net/anime/8147")
                val one = URI("http://anilist.co/anime/2994")

                // when
                anime.addRelations(four, two, three, one)

                // then
                assertThat(anime.relatedAnime).containsExactly(one, two, three, four)
            }

            @Test
            fun `cannot add a related anime if the links is already part of the sources`() {
                // given
                val link = URI("https://myanimelist.net/anime/1535")
                val anime = Anime(
                    _title =  "Death Note"
                ).addSources(link)

                // when
                anime.addRelations(link)

                // then
                assertThat(anime.relatedAnime).isEmpty()
            }
        }

        @Nested
        inner class RemoveRelationIfTests {

            @Test
            fun `successfully remove related anime`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")
                val anime = Anime(
                    _title =  "Death Note"
                ).addRelations(listOf(relatedAnime))

                // when
                anime.removeRelationIf { it.toString() == "https://myanimelist.net/anime/2994" }

                // then
                assertThat(anime.relatedAnime).isEmpty()
            }

            @Test
            fun `don't remove anything if predicate doesn't match anything`() {
                // given
                val relatedAnime = URI("https://myanimelist.net/anime/2994")
                val anime = Anime(
                    _title =  "Death Note"
                ).addRelations(listOf(relatedAnime))

                // when
                anime.removeRelationIf { it.toString().contains("anidb.net") }

                // then
                assertThat(anime.relatedAnime).containsExactly(relatedAnime)
            }
        }
    }

    @Nested
    inner class EqualityTests {

        @Test
        fun `is equal if source links are the same`() {
            // given
            val title =  "Death Note"
            val a = Anime(
                _title = title
            ).addSources(mutableListOf(URI("https://myanimelist.net/anime/1535")))


            val b = Anime(
                _title = title
            ).addSources(mutableListOf(URI("https://myanimelist.net/anime/1535")))

            // when
            val result = a == b

            // then
            assertThat(result).isTrue()
            assertThat(a.hashCode()).isEqualTo(b.hashCode())
        }

        @Test
        fun `is not equal if source links are different`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                _title =  title
            ).addSources(mutableListOf(URI("https://myanimelist.net/anime/1535")))

            val b = Anime(
                _title =  title
            ).addSources(
                    mutableListOf(
                        URI("https://myanimelist.net/anime/1535"),
                        URI("https://anidb.net/anime/4563")
                    )
            )

            // when
            val result = a == b

            // then
            assertThat(result).isFalse()
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode())
        }

        @Test
        fun `is equal if related anime are the same`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                _title =  title
            ).addRelations(mutableListOf(URI("https://myanimelist.net/anime/2994")))

            val b = Anime(
                _title =  title
            ).addRelations(mutableListOf(URI("https://myanimelist.net/anime/2994")))

            // when
            val result = a == b

            // then
            assertThat(result).isTrue()
            assertThat(a.hashCode()).isEqualTo(b.hashCode())
        }

        @Test
        fun `is not equal if related anime are different`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                _title =  title
            ).addRelations(mutableListOf(URI("https://myanimelist.net/anime/2994")))

            val b = Anime(
                _title =  title
            ).addRelations(
                    mutableListOf(
                        URI("https://myanimelist.net/anime/2994"),
                        URI("http://anilist.co/anime/2994")
                    )
            )

            // when
            val result = a == b

            // then
            assertThat(result).isFalse()
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode())
        }

        @Test
        fun `is equal if synonyms are the same`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                _title =  title
            ).addSynonyms(mutableListOf("Caderno da Morte"))

            val b = Anime(
                _title =  title
            ).addSynonyms(mutableListOf("Caderno da Morte"))

            // when
            val result = a == b

            // then
            assertThat(result).isTrue()
            assertThat(a.hashCode()).isEqualTo(b.hashCode())
        }

        @Test
        fun `is not equal if synonyms are different`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                _title =  title
            ).addSynonyms(mutableListOf("Caderno da Morte"))


            val b = Anime(
                _title =  title
            ).addSynonyms(
                    mutableListOf(
                            "Caderno da Morte",
                            "Quaderno della Morte"
                    )
            )

            // when
            val result = a == b

            // then
            assertThat(result).isFalse()
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode())
        }

        @Test
        fun `is equal if tags are the same`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                    _title =  title
            ).addTags(mutableListOf("comedy", "slice of life"))

            val b = Anime(
                    _title =  title
            ).addTags(
                    mutableListOf(
                            "slice of life",
                            "comedy"
                    )
            )

            // when
            val result = a == b

            // then
            assertThat(result).isTrue()
            assertThat(a.hashCode()).isEqualTo(b.hashCode())
        }

        @Test
        fun `is not equal if tags are different`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                    _title =  title
            ).addTags(mutableListOf("slice of life"))


            val b = Anime(
                    _title =  title
            ).addTags(
                    mutableListOf(
                            "slice of life",
                            "comedy"
                    )
            )

            // when
            val result = a == b

            // then
            assertThat(result).isFalse()
            assertThat(a.hashCode()).isNotEqualTo(b.hashCode())
        }

        @Test
        fun `is not equal if the other object is of a different type`() {
            // given
            val title  =  "Death Note"
            val a = Anime(
                    _title =  title
            ).addTags(mutableListOf("slice of life"))

            // when
            val result = a.equals(1)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class MergeTests {

        @Test
        fun `add title and synonyms of the other anime to this anime's synonyms`() {
            // given
            val anime = Anime(
                _title =  "Death Note"
            ).addSynonyms(mutableListOf("Caderno da Morte"))

            val other = Anime(
                _title =  "DEATH NOTE"
            ).addSynonyms(mutableListOf("Caderno da Morte", "Quaderno della Morte"))

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.title).isEqualTo(anime.title)
            assertThat(result.synonyms).containsExactly("Caderno da Morte", "DEATH NOTE", "Quaderno della Morte")
        }

        @Test
        fun `merge related anime and source links`() {
            // given
            val anime = Anime(
                _title =  "Death Note"
            ).addSources(
                    mutableListOf(
                        URI("https://myanimelist.net/anime/1535")
                    )
            ).addRelations(
                    mutableListOf(
                        URI("https://myanimelist.net/anime/2994")
                    )
            )

            val other = Anime(
                _title =  "Death Note"
            ).addSources(
                    mutableListOf(
                        URI("https://anidb.net/anime/4563")
                    )
            ).addRelations(
                    mutableListOf(
                        URI("https://anidb.net/anime/8146"),
                        URI("https://anidb.net/anime/8147")
                    )
            )

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.sources).containsExactly(
                URI("https://anidb.net/anime/4563"),
                URI("https://myanimelist.net/anime/1535")
            )
            assertThat(result.relatedAnime).containsExactly(
                URI("https://anidb.net/anime/8146"),
                URI("https://anidb.net/anime/8147"),
                URI("https://myanimelist.net/anime/2994")
            )
        }

        @Test
        fun `merge tags`() {
            // given
            val anime = Anime(
                _title =  "Death Note"
            ).addTags(mutableListOf("Psychological", "Thriller", "Shounen"))

            val other = Anime(
                _title =  "Death Note"
            ).addTags(mutableListOf("Mystery", "Police", "Psychological", "Supernatural", "Thriller"))

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.tags).containsExactly(
                "mystery",
                "police",
                "psychological",
                "shounen",
                "supernatural",
                "thriller"
            )
        }

        @Test
        fun `other direction - add title and synonyms of the other anime to this anime's synonyms`() {
            // given
            val anime = Anime(
                _title =  "Death Note"
            ).addSynonyms(mutableListOf("Caderno da Morte"))


            val other = Anime(
                _title =  "DEATH NOTE"
            ).addSynonyms(
                    mutableListOf(
                            "Caderno da Morte",
                            "Quaderno della Morte"
                    )
            )

            // when
            val result = other.mergeWith(anime)

            // then
            assertThat(result.title).isEqualTo(other.title)
            assertThat(result.synonyms).containsExactly("Caderno da Morte", "Death Note", "Quaderno della Morte")
        }

        @Test
        fun `other direction - merge related anime and source links`() {
            // given
            val anime = Anime(
                _title =  "Death Note"
            ).addSources(
                    mutableListOf(URI("https://myanimelist.net/anime/1535"))
            ).addRelations(
                    mutableListOf(URI("https://myanimelist.net/anime/2994"))
            )

            val other = Anime(
                _title =  "Death Note"
            ).addSources(
                    mutableListOf(URI("https://anidb.net/anime/4563"))
            ).addRelations(
                    mutableListOf(
                        URI("https://anidb.net/anime/8146"),
                        URI("https://anidb.net/anime/8147")
                    )
            )

            // when
            val result = other.mergeWith(anime)

            // then
            assertThat(result.sources).containsExactly(
                URI("https://anidb.net/anime/4563"),
                URI("https://myanimelist.net/anime/1535")
            )
            assertThat(result.relatedAnime).containsExactly(
                URI("https://anidb.net/anime/8146"),
                URI("https://anidb.net/anime/8147"),
                URI("https://myanimelist.net/anime/2994")
            )
        }

        @Test
        fun `adopt season of other if anime's season is undefined`() {
            // given
            val anime = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(season = UNDEFINED)
            )
            val other = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(season = SPRING)
            )

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.animeSeason.season).isEqualTo(SPRING)
        }

        @Test
        fun `keep anime's season other's season is different`() {
            // given
            val anime = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(season = SPRING)
            )
            val other = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(season = SUMMER)
            )

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.animeSeason.season).isEqualTo(SPRING)
        }

        @Test
        fun `adopt year of other if anime's year is unknown`() {
            // given
            val anime = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(_year = 0)
            )
            val other = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(_year = 2020)
            )

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.animeSeason.year).isEqualTo(2020)
        }

        @Test
        fun `keep anime's year other's year is different`() {
            // given
            val anime = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(_year = 2020)
            )
            val other = Anime(
                _title =  "Death Note",
                animeSeason = AnimeSeason(_year = 2019)
            )

            // when
            val result = anime.mergeWith(other)

            // then
            assertThat(result.animeSeason.year).isEqualTo(2020)
        }
    }

    @Nested
    inner class TagsTest {

        @Nested
        inner class AddTagsConstructorTests {

            @Test
            fun `tags added by constructor are set to lower case`() {
                // given
                val tag = "EXAMPLE"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf(tag), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isNotEqualTo(tag)
                assertThat(result.tags.first()).isEqualTo(tag.toLowerCase())
            }

            @Test
            fun `remove leading whitespace from title`() {
                // given
                val expectedTag = "example"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf(" $expectedTag"), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `remove tailing whitespace from title`() {
                // given
                val expectedTag = "example"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf("$expectedTag "), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace multiple whitespaces with a single whitespace in title`() {
                // given
                val expectedTag = "slice of life"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf("slice     of      life"), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace tab character with whitespace in title`() {
                // given
                val expectedTag = "slice of life"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf("slice\tof\tlife"), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace line feed character with whitespace in title`() {
                // given
                val expectedTag = "slice of life"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf("slice\nof\nlife"), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace carriage return line feed with whitespace in title`() {
                // given
                val expectedTag = "slice of life"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf("slice\r\nof\r\nlife"), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `don't add tag if it's an empty string`() {
                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf(EMPTY), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags).isEmpty()
            }

            @Test
            fun `don't add tag if it's a blank string`() {
                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf("     "), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags).isEmpty()
            }

            @Test
            fun `tags are sorted ascending`() {
                // given
                val tag1 = "a tag"
                val tag2 = "before the other"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf(tag2, tag1), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags).containsExactly(tag1, tag2)
            }

            @Test
            fun `tags is a distinct list`() {
                // given
                val tag1 = "a tag"
                val tag2 = "before the other"

                // when
                val result = Anime(
                    _title = "Test",
                    tags = SortedList(mutableListOf(tag2, tag1, tag1, tag2), STRING_COMPARATOR)
                )

                // then
                assertThat(result.tags).hasSize(2)
                assertThat(result.tags).containsExactly(tag1, tag2)
            }
        }

        @Nested
        inner class AddTagsTests {

            @Test
            fun `tags added are set to lower case`() {
                // given
                val tag = "EXAMPLE"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf(tag))

                // then
                assertThat(anime.tags.first()).isNotEqualTo(tag)
                assertThat(anime.tags.first()).isEqualTo(tag.toLowerCase())
            }

            @Test
            fun `remove leading whitespace from title`() {
                // given
                val expectedTag = "example"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf(" $expectedTag"))

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `remove tailing whitespace from title`() {
                // given
                val expectedTag = "example"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf("$expectedTag "))

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace multiple whitespaces with a single whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf("slice     of      life"))

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace tab character with whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf("slice\tof\tlife"))

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace line feed character with whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf("slice\nof\nlife"))

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace carriage return line feed with whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf("slice\r\nof\r\nlife"))

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `don't add tag if it's an empty string`() {
                // given
                val anime = Anime("Test")

                // when
                anime.addTags(listOf(EMPTY))

                // then
                assertThat(anime.tags).isEmpty()
            }

            @Test
            fun `don't add tag if it's a blank string`() {
                // given
                val anime = Anime("Test")

                // when
                anime.addTags(listOf("     "))

                // then
                assertThat(anime.tags).isEmpty()
            }

            @Test
            fun `tags are sorted ascending`() {
                // given
                val tag1 = "a tag"
                val tag2 = "before the other"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf(tag2, tag1))

                // then
                assertThat(anime.tags).containsExactly(tag1, tag2)
            }

            @Test
            fun `tags is a distinct list`() {
                // given
                val tag1 = "a tag"
                val tag2 = "before the other"
                val anime = Anime("Test")

                // when
                anime.addTags(listOf(tag2, tag1, tag1, tag2))

                // then
                assertThat(anime.tags).hasSize(2)
                assertThat(anime.tags).containsExactly(tag1, tag2)
            }
        }

        @Nested
        inner class AddTagsVarargTests {

            @Test
            fun `tags added are set to lower case`() {
                // given
                val tag = "EXAMPLE"
                val anime = Anime("Test")

                // when
                anime.addTags(tag)

                // then
                assertThat(anime.tags.first()).isNotEqualTo(tag)
                assertThat(anime.tags.first()).isEqualTo(tag.toLowerCase())
            }

            @Test
            fun `remove leading whitespace from title`() {
                // given
                val expectedTag = "example"
                val anime = Anime("Test")

                // when
                anime.addTags(" $expectedTag")

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `remove tailing whitespace from title`() {
                // given
                val expectedTag = "example"
                val anime = Anime("Test")

                // when
                anime.addTags("$expectedTag ")

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace multiple whitespaces with a single whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags("slice     of      life")

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace tab character with whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags("slice\tof\tlife")

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace line feed character with whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags("slice\nof\nlife")

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `replace carriage return line feed with whitespace in title`() {
                // given
                val expectedTag = "slice of life"
                val anime = Anime("Test")

                // when
                anime.addTags("slice\r\nof\r\nlife")

                // then
                assertThat(anime.tags.first()).isEqualTo(expectedTag)
            }

            @Test
            fun `don't add tag if it's an empty string`() {
                // given
                val anime = Anime("Test")

                // when
                anime.addTags(EMPTY)

                // then
                assertThat(anime.tags).isEmpty()
            }

            @Test
            fun `don't add tag if it's a blank string`() {
                // given
                val anime = Anime("Test")

                // when
                anime.addTags("     ")

                // then
                assertThat(anime.tags).isEmpty()
            }

            @Test
            fun `tags are sorted ascending`() {
                // given
                val tag1 = "a tag"
                val tag2 = "before the other"
                val anime = Anime("Test")

                // when
                anime.addTags(tag2, tag1)

                // then
                assertThat(anime.tags).containsExactly(tag1, tag2)
            }

            @Test
            fun `tags is a distinct list`() {
                // given
                val tag1 = "a tag"
                val tag2 = "before the other"
                val anime = Anime("Test")

                // when
                anime.addTags(tag2, tag1, tag1, tag2)

                // then
                assertThat(anime.tags).hasSize(2)
                assertThat(anime.tags).containsExactly(tag1, tag2)
            }
        }
    }

    @Nested
    inner class DurationTests {

        @Test
        fun `default duration is 0 seconds`() {
            // given
            val expectedDuration = Duration(0, SECONDS)

            // when
            val result = Anime("Death Note")

            // then
            assertThat(result.duration).isEqualTo(expectedDuration)
        }

        @Test
        fun `setting a duration of 10 seconds`() {
            // when
            val result = Duration(10, SECONDS)

            // then
            assertThat(result.duration).isEqualTo(10)
            assertThat(result.toString()).isEqualTo("10 seconds")
        }

        @Test
        fun `duration of a minute is equal to a duration of 60 seconds`() {
            // given
            val durationInSeconds = Duration(60, SECONDS)

            // when
            val result = Duration(1, MINUTES)

            // then
            assertThat(result).isEqualTo(durationInSeconds)
            assertThat(result.duration).isEqualTo(60)
            assertThat(result.toString()).isEqualTo("60 seconds")
        }

        @Test
        fun `duration of an hour is equal to a duration of 60 minutes`() {
            // given
            val durationInMinutes = Duration(60, MINUTES)

            // when
            val result = Duration(1, HOURS)

            // then
            assertThat(result).isEqualTo(durationInMinutes)
            assertThat(result.duration).isEqualTo(3600)
            assertThat(result.toString()).isEqualTo("3600 seconds")
        }

        @Test
        fun `returns false on equals check if other object is not of the same type`() {
            // given
            val duration = Duration(60, MINUTES)

            // when
            val result = duration.equals(EMPTY)

            // then
            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class AnimeSeasonTests {

        @Test
        fun `default year is 0 indicating unknown and season is undefined`() {
            // when
            val result = Anime("Death Note")

            // then
            assertThat(result.animeSeason.year).isZero()
            assertThat(result.animeSeason.isYearOfPremiereUnknown()).isTrue()
            assertThat(result.animeSeason.season).isEqualTo(UNDEFINED)
        }
    }

    @Nested
    inner class SeasonTests {

        @Test
        fun `create season SPRING of string`() {
            // given
            val value = "SpRiNg"

            // when
            val result = Companion.of(value)

            // then
            assertThat(result).isEqualTo(SPRING)
        }

        @Test
        fun `create season SUMMER of string`() {
            // given
            val value = "SuMmEr"

            // when
            val result = Companion.of(value)

            // then
            assertThat(result).isEqualTo(SUMMER)
        }

        @Test
        fun `create season FALL of string`() {
            // given
            val value = "FaLl"

            // when
            val result = Companion.of(value)

            // then
            assertThat(result).isEqualTo(FALL)
        }

        @Test
        fun `create season WINTER of string`() {
            // given
            val value = "WiNtEr"

            // when
            val result = Companion.of(value)

            // then
            assertThat(result).isEqualTo(WINTER)
        }

        @Test
        fun `create season UNDEFINED of string`() {
            // given
            val value = "UnDeFiNeD"

            // when
            val result = Companion.of(value)

            // then
            assertThat(result).isEqualTo(UNDEFINED)
        }

        @Test
        fun `create season UNDEFINED of any string that cannot be matched`() {
            // given
            val value = "sgwegesdsdsf"

            // when
            val result = Companion.of(value)

            // then
            assertThat(result).isEqualTo(UNDEFINED)
        }
    }

    @Nested
    inner class ToStringTests {

        @Test
        fun `create formatted string listing all properties`() {
            // given
            val anime = Anime(
                    _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                    type = Special,
                    episodes = 1,
                    status = FINISHED,
                    animeSeason = AnimeSeason(
                            season = SUMMER,
                            _year = 2009
                    ),
                    picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                    thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg"),
                    duration = Duration(2, MINUTES)
            ).apply {
                addSources(listOf(URI("https://myanimelist.net/anime/6351")))
                addSynonyms(
                        listOf(
                                "Clannad ~After Story~: Another World, Kyou Chapter",
                                "Clannad: After Story OVA",
                                "クラナド　アフターストーリー　もうひとつの世界　杏編"
                        )
                )
                addRelations(listOf(URI("https://myanimelist.net/anime/2167")))
                addTags(
                        listOf(
                                "comedy",
                                "drama",
                                "romance",
                                "school",
                                "slice of life",
                                "supernatural"
                        )
                )
            }

            // when
            val result = anime.toString()

            // then
            assertThat(result).isEqualTo(
                """
                    Anime(
                      sources = [https://myanimelist.net/anime/6351]
                      title = Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen
                      synonyms = [Clannad ~After Story~: Another World, Kyou Chapter, Clannad: After Story OVA, クラナド　アフターストーリー　もうひとつの世界　杏編]
                      type = Special
                      episodes = 1
                      status = FINISHED
                      animeSeason = AnimeSeason(season=SUMMER, _year=2009)
                      picture = https://cdn.myanimelist.net/images/anime/10/19621.jpg
                      thumbnail = https://cdn.myanimelist.net/images/anime/10/19621t.jpg
                      duration = 120 seconds
                      relations = [https://myanimelist.net/anime/2167]
                      tags = [comedy, drama, romance, school, slice of life, supernatural]
                    )
                """.trimIndent()
            )
        }
    }
}