package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.models.AnimeSeason.Season.UNDEFINED
import java.time.LocalDate

/**
 * @since 1.0.0
 */
public typealias Year = Int

/**
 * Year of the first japanese anime. See [Wikipedia](https://en.wikipedia.org/wiki/Katsud%C5%8D_Shashin)
 */
private const val YEAR_OF_THE_FIRST_ANIME: Year = 1907

/**
 * Defines the season in which an anime premiered or has been published.
 * @since 1.0.0
 * @property season **Default** is [UNDEFINED]
 * @property _year Year in the format `YYYY`. Requires a value between [YEAR_OF_THE_FIRST_ANIME] and the current year + 5. Otherwise an exception is thrown.
 * **Default** is `0` indicating unknown year.
 */
public data class AnimeSeason(
    var season: Season = UNDEFINED,
    private var _year: Year = 0
) {
    /**
     * Year in the format `YYYY`. Requires a value between [YEAR_OF_THE_FIRST_ANIME] and the current year + 5. Otherwise an exception is thrown.
     * Value can also be `0` indicating unknown year.
     * @since 1.0.0
     */
    var year : Year
        get() = _year
        set(value) {
            validateYear(value)
            _year = value
        }

    init {
        validateYear(_year)
    }

    /**
     * @since 1.0.0
     * @return `true` if the year is unknown which means that it's `0`
     */
    public fun isYearOfPremiereUnknown(): Boolean = _year == 0

    /**
     * @since 1.0.0
     * @return `true` if the year is known which means that it's `0`
     */
    public fun isYearOfPremiereKnown(): Boolean = _year != 0

    private fun validateYear(value: Year) {
        val isYearAfterVeryFirstAnimeRelease = value >= YEAR_OF_THE_FIRST_ANIME
        val isYearNotTooFarInTheFuture = value <= LocalDate.now().year + 5
        val isYearWithinRange = isYearAfterVeryFirstAnimeRelease && isYearNotTooFarInTheFuture
        val isYearUnknown = value == 0

        require(isYearUnknown || isYearWithinRange) { "Year of premiere [$value] is not valid" }
    }

    /**
     * @since 1.0.0
     */
    public enum class Season {
        UNDEFINED,
        SPRING,
        SUMMER,
        FALL,
        WINTER;

        public companion object {
            public fun of(value: String): Season {
                return values().find { it.toString().trim().equals(value, ignoreCase = true) } ?: UNDEFINED
            }
        }
    }
}