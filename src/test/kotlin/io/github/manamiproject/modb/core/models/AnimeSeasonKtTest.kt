package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

internal class AnimeSeasonKtTest {

    @Nested
    inner class SeasonTests {

        @Test
        fun `'SPRING' by string`() {
            // given
            val value = "SpRiNg"

            // when
            val result = AnimeSeason.Season.of(value)

            // then
            assertThat(result).isEqualTo(SPRING)
        }

        @Test
        fun `'SUMMER' by string`() {
            // given
            val value = "SuMmEr"

            // when
            val result = AnimeSeason.Season.of(value)

            // then
            assertThat(result).isEqualTo(SUMMER)
        }

        @Test
        fun `'FALL' by string`() {
            // given
            val value = "FaLl"

            // when
            val result = AnimeSeason.Season.of(value)

            // then
            assertThat(result).isEqualTo(FALL)
        }

        @Test
        fun `'WINTER' by string`() {
            // given
            val value = "WiNtEr"

            // when
            val result = AnimeSeason.Season.of(value)

            // then
            assertThat(result).isEqualTo(WINTER)
        }

        @Test
        fun `'UNDEFINED' by string`() {
            // given
            val value = "UnDeFiNeD"

            // when
            val result = AnimeSeason.Season.of(value)

            // then
            assertThat(result).isEqualTo(UNDEFINED)
        }

        @Test
        fun `'UNDEFINED' as failover for any non-matching string`() {
            // given
            val value = "non-matching-string"

            // when
            val result = AnimeSeason.Season.of(value)

            // then
            assertThat(result).isEqualTo(UNDEFINED)
        }

        @Test
        fun `default is 'UNDEFINED'`() {
            // when
            val result = AnimeSeason()

            // then
            assertThat(result.season).isEqualTo(UNDEFINED)
        }
    }

    @Nested
    inner class YearTests {

        @Nested
        inner class ConstructorTests {

            @Test
            fun `year cannot be before 1900`() {
                // when
                val result = assertThrows<IllegalArgumentException> {
                    AnimeSeason(
                        _year = 1899
                    )
                }

                // then
                assertThat(result).hasMessage("Year of premiere [1899] is not valid")
            }

            @Test
            fun `year can be from 1907 on, because that is the year of the first anime in japan`() {
                // when
                val result = AnimeSeason(
                    _year = 1907
                )

                // then
                assertThat(result.isYearOfPremiereUnknown()).isFalse()
                assertThat(result.isYearOfPremiereKnown()).isTrue()
            }

            @Test
            fun `year cannot be more than 6 years from now`() {
                // given
                val year = LocalDate.now().year + 6

                // when
                val result = assertThrows<IllegalArgumentException> {
                    AnimeSeason(
                        _year = year
                    )
                }

                // then
                assertThat(result).hasMessage("Year of premiere [$year] is not valid")
            }

            @Test
            fun `year can be up to five years from now`() {
                // given
                val year = LocalDate.now().year + 5

                // when
                val result = AnimeSeason(
                    _year = year
                )

                // then
                assertThat(result.year).isEqualTo(year)
            }

            @Test
            fun `default year is 0 and means that no particular year is known`() {
                // given
                val animeSeason = AnimeSeason()

                // when
                val isYearOfPremiereUnknown = animeSeason.isYearOfPremiereUnknown()
                val isYearOfPremiereKnown = animeSeason.isYearOfPremiereKnown()

                // then
                assertThat(animeSeason.year).isZero()
                assertThat(isYearOfPremiereUnknown).isTrue()
                assertThat(isYearOfPremiereKnown).isFalse()
            }
        }

        @Nested
        inner class SetterTests {

            @Test
            fun `year cannot be before 1900`() {
                // given
                val animeSeason = AnimeSeason()

                // when
                val result = assertThrows<IllegalArgumentException> {
                    animeSeason.year = 1899
                }

                // then
                assertThat(result).hasMessage("Year of premiere [1899] is not valid")
            }

            @Test
            fun `year can be from 1907 on, because that is the year of the first anime in japan`() {
                // given
                val animeSeason = AnimeSeason()

                // when
                animeSeason.year = 1907

                // then
                assertThat(animeSeason.isYearOfPremiereUnknown()).isFalse()
                assertThat(animeSeason.isYearOfPremiereKnown()).isTrue()
            }

            @Test
            fun `year cannot be more than 6 years from now`() {
                // given
                val animeSeason = AnimeSeason()
                val year = LocalDate.now().year + 6

                // when
                val result = assertThrows<IllegalArgumentException> {
                    animeSeason.year = year
                }

                // then
                assertThat(result).hasMessage("Year of premiere [$year] is not valid")
            }

            @Test
            fun `year can be up to five years from now`() {
                // given
                val animeSeason = AnimeSeason()
                val year = LocalDate.now().year + 5

                // when
                animeSeason.year = year

                // then
                assertThat(animeSeason.year).isEqualTo(year)
            }

            @Test
            fun `default year is 0 and means that no particular year is known`() {
                // given
                val animeSeason = AnimeSeason()

                // when
                val isYearOfPremiereUnknown = animeSeason.isYearOfPremiereUnknown()
                val isYearOfPremiereKnown = animeSeason.isYearOfPremiereKnown()

                // then
                assertThat(animeSeason.year).isZero()
                assertThat(isYearOfPremiereUnknown).isTrue()
                assertThat(isYearOfPremiereKnown).isFalse()
            }
        }
    }
}