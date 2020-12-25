package io.github.manamiproject.modb.core.models

import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.collections.SortedList.Companion.STRING_COMPARATOR
import io.github.manamiproject.modb.core.collections.SortedList.Companion.URI_COMPARATOR
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime.Type.TV
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.UNDEFINED
import java.net.URI


/**
 * @since 1.0.0
 */
public typealias Episodes = Int

/**
 * @since 3.0.0
 */
public typealias Tag = String

/**
 * @since 3.0.0
 */
public typealias Title = String


/**
 * @since 3.1.0
 * @property _title Main title. Must not be blank.
 * @property sources Duplicate-free list of related anime. Sorted ascending.
 * @property synonyms Duplicate-free list of related anime. Synonyms are case sensitive and sorted ascending.
 * @property type Distribution type. **Default** is [TV]
 * @property episodes Number of episodes. **Default** is `0`
 * @property status Publishing status. **Default** is [UNKNOWN]
 * @property animeSeason In which season did the anime premiere
 * @property picture [URI] to a (large) poster/cover. **Default** is the not-found-pic from MAL.
 * @property thumbnail [URI] to a thumbnail poster/cover. **Default** is the not-found-pic from MAL.
 * @property duration Duration of an anime having one episode or average duration of an episode if the anime has more than one episode.
 * @property relatedAnime Duplicate-free list of related anime. Sorted ascending.
 * @property tags Duplicate-free list of tags. Sorted ascending. All tags are lower case.
 * @throws IllegalArgumentException if _title is blank
 */
public data class Anime(
    private var _title: Title,
    val sources: SortedList<URI> = SortedList(comparator = URI_COMPARATOR),
    val synonyms: SortedList<Title> = SortedList(comparator = STRING_COMPARATOR),
    val type: Type = TV,
    val episodes: Episodes = 0,
    val status: Status = Status.UNKNOWN,
    val animeSeason: AnimeSeason = AnimeSeason(),
    val picture: URI = URI("https://cdn.myanimelist.net/images/qm_50.gif"),
    val thumbnail: URI = URI("https://cdn.myanimelist.net/images/qm_50.gif"),
    val duration: Duration = Duration.UNKNOWN,
    val relatedAnime: SortedList<URI> = SortedList(comparator = URI_COMPARATOR),
    val tags: SortedList<Tag> = SortedList(comparator = STRING_COMPARATOR),
) {

    /**
     * Main title.
     * @since 1.0.0
     */
    val title: Title
        get() = _title

    init {
        require(_title.isNotBlank()) { "Title cannot be blank." }
        _title = cleanupTitle(_title)

        require(episodes >= 0) { "Episodes cannot have a negative value." }

        val uncheckedSources: Collection<URI> = sources.toList()
        sources.clear()
        addSources(uncheckedSources)

        val uncheckedSynonyms: Collection<Title> = synonyms.toList()
        synonyms.clear()
        addSynonyms(uncheckedSynonyms)

        val uncheckedRelatedAnime: Collection<URI> = relatedAnime.toList()
        relatedAnime.clear()
        addRelations(uncheckedRelatedAnime)

        val uncheckedTags: Collection<Tag> = tags.toList()
        tags.clear()
        addTags(uncheckedTags)
    }

    /**
     * Add additional synonyms to the existing list. Duplicates are being ignored.
     * Comparison for this is case sensitive. This will **not** override [synonyms].
     * The value which is present in [title] cannot be added.
     * @since 3.1.0
     * @param synonym Synonyms to be added
     * @return Same instance
     */
    public fun addSynonyms(vararg synonym: Title): Anime = addSynonyms(synonym.toList())

    /**
     * Add additional synonyms to the existing list. Duplicates are being ignored.
     * Comparison for this is case sensitive. This will **not** override [synonyms].
     * The value which is present in [title] cannot be added.
     * @since 1.0.0
     * @param synonyms List of synonyms
     * @return Same instance
     */
    public fun addSynonyms(synonyms: Collection<Title>): Anime {
        synonyms.asSequence()
            .map { cleanupTitle(it) }
            .filter { it.isNotBlank() }
            .filter { it != _title }
            .filter { !this.synonyms.contains(it) }
            .forEach { this.synonyms.add(it) }

        return this
    }

    /**
     * Add additional sources to the existing list. This will **not** override [sources].
     * Duplicates are being ignored.
     * @since 3.1.0
     * @param source Sources to be added
     * @return Same instance
     */
    public fun addSources(vararg source: URI): Anime = addSources(source.toList())

    /**
     * Add additional sources to the existing list. This will **not** override [sources].
     * Duplicates are being ignored.
     * @since 3.0.0
     * @param sources List of sources
     * @return Same instance
     */
    public fun addSources(sources: Collection<URI>): Anime {
        sources.asSequence()
            .filterNot { this.sources.contains(it) }
            .forEach { this.sources.add(it) }

        removeRelationIf { sources.contains(it) }

        return this
    }

    /**
     * Add additional related anime to the existing list. This will **not** override [relatedAnime].
     * Duplicates are being ignored.
     * @since 3.1.0
     * @param relatedAnime List of related anime
     * @return Same instance
     */
    public fun addRelations(vararg relatedAnime: URI): Anime = addRelations(relatedAnime.toList())

    /**
     * Add additional related anime to the existing list. This will **not** override [relatedAnime].
     * Duplicates are being ignored.
     * @since 3.0.0
     * @param relatedAnime List of related anime
     * @return Same instance
     */
    public fun addRelations(relatedAnime: Collection<URI>): Anime {
        relatedAnime.asSequence()
            .filter { !this.relatedAnime.contains(it) && !sources.contains(it) }
            .forEach { this.relatedAnime.add(it) }

        return this
    }

    /**
     * Add additional tags to the existing list. This will **not** override [tags].
     * Duplicates are being ignored.
     * @since 3.1.0
     * @param tag List of tags
     * @return Same instance
     */
    public fun addTags(vararg tag: Tag): Anime = addTags(tag.toList())

    /**
     * Add additional tags to the existing list. This will **not** override [tags].
     * Duplicates are being ignored.
     * @since 1.0.0
     * @param tags List of tags
     * @return Same instance
     */
    public fun addTags(tags: Collection<Tag>): Anime {
        tags.asSequence()
            .map { cleanupTitle(it) }
            .filter { it.isNotBlank() }
            .map { it.toLowerCase() }
            .filter { !this.tags.contains(it) }
            .forEach { this.tags.add(it) }

        return this
    }

    /**
     * Removes an [URI] from [relatedAnime] if the given condition matches.
     * @since 3.0.0
     * @param condition If the this condition applied to a related anime uri matches, then the [URI] will be removed from [relatedAnime]
     * @return Same instance
     */
    public fun removeRelationIf(condition: (URI) -> Boolean): Anime {
        relatedAnime.removeIf { condition.invoke(it) }
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
    public fun mergeWith(anime: Anime): Anime {
        addSynonyms(listOf(anime.title))
        addSynonyms(anime.synonyms)
        addSources(anime.sources)
        addRelations(anime.relatedAnime)
        addTags(anime.tags)

        if (animeSeason.season == UNDEFINED && anime.animeSeason.season != UNDEFINED) {
            animeSeason.season = anime.animeSeason.season
        }

        if (animeSeason.isYearOfPremiereUnknown() && anime.animeSeason.isYearOfPremiereKnown()) {
            animeSeason.year = anime.animeSeason.year
        }

        return this
    }

    private fun cleanupTitle(original: Title): Title {
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

    override fun toString(): String {
        return """
            Anime(
              sources = $sources
              title = $_title
              synonyms = $synonyms
              type = $type
              episodes = $episodes
              status = $status
              animeSeason = $animeSeason
              picture = $picture
              thumbnail = $thumbnail
              duration = $duration
              relations = $relatedAnime
              tags = $tags
            )
        """.trimIndent()
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
    public enum class Type {
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
    public enum class Status {
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