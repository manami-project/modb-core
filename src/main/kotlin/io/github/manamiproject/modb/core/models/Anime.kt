package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.collections.SortedList.Companion.STRING_COMPARATOR
import io.github.manamiproject.modb.core.collections.SortedList.Companion.URL_COMPARATOR
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime.Status.UNKNOWN
import io.github.manamiproject.modb.core.models.Anime.Type.TV
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.UNDEFINED
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.SECONDS
import java.net.URL
import java.util.*

/**
 * @since 1.0.0
 */
typealias Episodes = Int

/**
 * @since 1.0.0
 * @param _title Main title. Must not be blank.
 * @param type Distribution type. **Default** is [TV]
 * @param episodes Number of episodes. **Default** is `0`
 * @param status Publishing status. **Default** is [UNKNOWN]
 * @param animeSeason In which season did the anime premiere
 * @param picture URL to a (large) poster/cover
 * @param thumbnail URL to a thumbnail poster/cover
 * @param duration Duration of an anime having one episode or average duration of an episode if the anime has more than one episode.
 * @throws IllegalArgumentException if _title is blank
 */
data class Anime(
    private var _title: String,
    val type: Type = TV,
    val episodes: Episodes = 0,
    val status: Status = UNKNOWN,
    val animeSeason: AnimeSeason = AnimeSeason(),
    val picture: URL = URL("https://cdn.myanimelist.net/images/qm_50.gif"),
    val thumbnail: URL = URL("https://cdn.myanimelist.net/images/qm_50.gif"),
    val duration: Duration = Duration(0, SECONDS)
) {

    /**
     * Main title.
     * @since 1.0.0
     */
    val title: String
        get() = _title

    /**
     * Duplicate-free list of related anime. Sorted ascending.
     * @since 1.0.0
     */
    val sources: List<URL>
        get() = _sources
    private var _sources: SortedList<URL> = SortedList(comparator = URL_COMPARATOR)

    /**
     * Duplicate-free list of related anime. Synonyms are case sensitive and sorted ascending.
     * @since 1.0.0
     */
    val synonyms: List<String>
        get() = _synonyms
    private var _synonyms: SortedList<String> = SortedList(comparator = STRING_COMPARATOR)

    /**
     * Duplicate-free list of related anime. Sorted ascending.
     * @since 1.0.0
     */
    val relatedAnime: List<URL>
        get() = _relatedAnime
    private var _relatedAnime: SortedList<URL> = SortedList(comparator = URL_COMPARATOR)

    /**
     * Duplicate-free list of tags. Sorted ascending. All tags are lower case.
     * @since 1.0.0
     */
    val tags: List<String>
        get() = _tags
    private var _tags: SortedList<String> = SortedList(comparator = STRING_COMPARATOR)

    init {
        require(_title.isNotBlank()) { "Title cannot be blank." }
        _title = cleanupTitle(_title)
    }

    /**
     * Add additional synonyms to the existing list. This will **not** override [synonyms].
     * @since 1.0.0
     * @param synonyms List of synonyms
     * @return Same instance
     */
    fun addSynonyms(synonyms: List<String>): Anime {
        synonyms.asSequence()
            .map { cleanupTitle(it) }
            .filter { it.isNotBlank() }
            .filter { it != _title }
            .filter { !_synonyms.contains(it) }
            .forEach { _synonyms.add(it) }

        return this
    }

    /**
     * Add additional sources to the existing list. This will **not** override [sources].
     * @since 1.0.0
     * @param sources List of sources
     * @return Same instance
     */
    fun addSources(sources: List<URL>): Anime {
        sources.asSequence()
            .filter { !_sources.contains(it) }
            .forEach { _sources.add(it) }

        return this
    }

    /**
     * Add additional related anime to the existing list. This will **not** override [relatedAnime].
     * @since 1.0.0
     * @param relatedAnime List of related anime
     * @return Same instance
     */
    fun addRelations(relatedAnime: List<URL>): Anime {
        relatedAnime.asSequence()
            .filter { !_relatedAnime.contains(it) && !_sources.contains(it) }
            .forEach { _relatedAnime.add(it) }

        return this
    }

    /**
     * Add additional tags to the existing list. This will **not** override [tags].
     * @since 1.0.0
     * @param tags List of tags
     * @return Same instance
     */
    fun addTags(tags: List<String>): Anime {
        tags.asSequence()
            .map { cleanupTitle(it) }
            .filter { it.isNotBlank() }
            .map { it.toLowerCase() }
            .filter { !_tags.contains(it) }
            .forEach { _tags.add(it) }

        return this
    }

    /**
     * Removes an [URL] from [relatedAnime] if the given condition matches.
     * @since 1.0.0
     * @param condition If the this condition applied to a related anime url matches, then the [URL] will be removed from [relatedAnime]
     * @return Same instance
     */
    fun removeRelationIf(condition: (URL) -> Boolean): Anime {
        _relatedAnime.removeIf { condition.invoke(it) }
        return this
    }

    /**
     * + Title and synonyms of the given [Anime] will both be added to the [synonyms] of this instance.
     * + All sources of the given [Anime] will be added to the [sources] of this instance.
     * + All related anime of the given [Anime] will be added to the [relatedAnime] of this instance.
     * + All tags of the given [Anime] will be added to the [tags] of this instance.
     * + In case the season of this instance's [animeSeason] is [UNDEFINED], the season of the given [Anime] will be applied.
     * + In case the year of this instance's [animeSeason] is unknown, the year if the given [Anime] will be applied.
     * @since 1.0.0
     * @param anime
     * @return Same instance
     */
    fun mergeWith(anime: Anime): Anime {
        addSynonyms(listOf(anime.title))
        addSynonyms(anime.synonyms)
        addSources(anime.sources)
        addRelations(anime._relatedAnime)
        addTags(anime.tags)

        if (animeSeason.season == UNDEFINED && anime.animeSeason.season != UNDEFINED) {
            animeSeason.season = anime.animeSeason.season
        }

        if (animeSeason.isYearOfPremiereUnknown() && anime.animeSeason.isYearOfPremiereKnown()) {
            animeSeason.year = anime.animeSeason.year
        }

        return this
    }

    private fun cleanupTitle(original: String): String {
        var editedTitle = original

        REPLACEMENTS.forEach { replacement ->
            if (editedTitle.contains(Regex(replacement))) {
                log.debug("Identified [{}] in [{}]", replacement, editedTitle)
                log.debug("Changed: [{}]", editedTitle)
                editedTitle = editedTitle.replace(Regex(replacement), " ")
                log.debug("To     : [{}]", editedTitle)
            }
        }

        if (editedTitle.startsWith(WHITESPACE) || editedTitle.endsWith(WHITESPACE)) {
            log.debug("Identified leading or trailing space in [{}]", editedTitle)
            log.debug("Changed: [{}]", editedTitle)
            editedTitle = editedTitle.trim()
            log.debug("To     : [{}]", editedTitle)
        }

        return editedTitle
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Anime) return false

        return _title == other.title
                && type == other.type
                && episodes == other.episodes
                && status == other.status
                && animeSeason == other.animeSeason
                && picture == other.picture
                && thumbnail == other.thumbnail
                && duration == other.duration
                && _sources.toList() == other.sources.toList()
                && _synonyms.toList() == other.synonyms.toList()
                && _relatedAnime.toList() == other.relatedAnime.toList()
                && _tags.toList() == other.tags.toList()
    }

    override fun hashCode(): Int = Objects.hashCode(this)

    private companion object {
        private val log by LoggerDelegate()

        private const val WHITESPACE = ' '
        private val REPLACEMENTS = listOf("\r\n", "\n", "\t", " {2,}")
    }

    /**
     * Distribution type of an anime.
     * @since 1.0.0
     */
    enum class Type {
        TV,
        Movie,
        /** Original Video Animation. See [Wikipedia](https://en.wikipedia.org/wiki/Original_video_animation) */
        OVA,
        /** Original Net Animation. See [Wikipedia](https://en.wikipedia.org/wiki/Original_net_animation) */
        ONA,
        Special
    }

    /**
     * Distribution status of an anime.
     * @since 1.0.0
     */
    enum class Status {
        /** Finished airing or has been released completely. */
        FINISHED,
        /** Currently airing or releasing. */
        CURRENTLY,
        /** Not yet released or aired. */
        UPCOMING,
        /** Status is unknown. */
        UNKNOWN
    }
}