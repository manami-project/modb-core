package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.extensions.doIfNotEmpty
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime.Status.UNKNOWN
import io.github.manamiproject.modb.core.models.Anime.Type.TV
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.UNDEFINED
import java.net.URL

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
 * @param _sources Direct links to the anime on the website of meta data providers. **Example:** `Death Note` on myanimelist.net: `https://myanimelist.net/anime/1535`
 * @param _synonyms List of multilingual synonyms
 * @param _relatedAnime Direct links to anime on the website if meta data providers. These anime are directly related to this one like a sequel or a movie to a series.
 * @param _tags List of tags describing the anime. They can relate to the anime's content or define the genres
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
    val duration: Duration = Duration(0, Duration.TimeUnit.SECONDS),
    private var _sources: MutableList<URL> = mutableListOf(),
    private var _synonyms: MutableList<String> = mutableListOf(),
    private var _relatedAnime: MutableList<URL> = mutableListOf(),
    private var _tags: MutableList<String> = mutableListOf()
) {

    @Transient
    private val urlComparator = Comparator<URL> { o1, o2 -> o1.toString().compareTo(o2.toString()) }

    init {
        require(_title.isNotBlank()) { "Title cannot be blank." }

        cleanupTitleAndSynonyms()

        _relatedAnime.removeIf { _sources.contains(it) }

        cleanupTags()
        distinctListEntries()
        sortLists()
    }

    /**
     * Main title.
     * @since 1.0.0
     */
    val title: String
        get() = _title

    /**
     * Duplicate-free list of related anime. Synonyms are case sensitive and sorted ascending.
     * @since 1.0.0
     */
    val synonyms: List<String>
        get() = _synonyms

    /**
     * Duplicate-free list of related anime. Sorted ascending.
     * @since 1.0.0
     */
    val sources: List<URL>
        get() = _sources

    /**
     * Duplicate-free list of related anime. Sorted ascending.
     * @since 1.0.0
     */
    val relatedAnime: List<URL>
        get() = _relatedAnime

    /**
     * Duplicate-free list of tags. Sorted ascending. All tags are lower case.
     * @since 1.0.0
     */
    val tags: List<String>
        get() = _tags

    /**
     * Add additional synonyms to the existing list. This will **not** override [synonyms].
     * @since 1.0.0
     * @param synonyms List of synonyms
     * @return Same instance
     */
    fun addSynonyms(synonyms: List<String>): Anime {
        synonyms.asSequence()
            .map { cleanupString(it) }
            .filter { it.isNotBlank() }
            .filter { it != title }
            .filter { !_synonyms.contains(it) }
            .map { _synonyms.add(it) }
            .doIfNotEmpty { _synonyms.sort() }

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
            .map { _sources.add(it) }
            .doIfNotEmpty { _sources.sortWith(urlComparator) }

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
            .map { _relatedAnime.add(it) }
            .doIfNotEmpty { _relatedAnime.sortWith(urlComparator) }

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
            .map { cleanupString(it) }
            .filter { it.isNotBlank() }
            .map { it.toLowerCase() }
            .filter { !_tags.contains(it) }
            .map { _tags.add(it) }
            .doIfNotEmpty { _tags.sort() }

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
        _relatedAnime.sortWith(urlComparator)

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

    private fun cleanupString(original: String): String {
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

    private fun cleanupTitleAndSynonyms() {
        _title = cleanupString(_title)
        _synonyms = _synonyms.asSequence()
            .map { cleanupString(it) }
            .filter { it.isNotBlank() }
            .toMutableList()
        _synonyms.removeIf { it == _title }
    }

    private fun cleanupTags() {
        _tags = _tags.asSequence()
            .map { cleanupString(it) }
            .filter { it.isNotBlank() }
            .map { it.toLowerCase() }
            .toMutableList()
    }

    private fun distinctListEntries() {
        _synonyms = _synonyms.distinct().toMutableList()
        _sources = _sources.distinct().toMutableList()
        _relatedAnime = _relatedAnime.distinct().toMutableList()
        _tags = _tags.distinct().toMutableList()
    }

    private fun sortLists() {
        _synonyms.sort()
        _sources.sortWith(urlComparator)
        _relatedAnime.sortWith(urlComparator)
        _tags.sort()
    }

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