package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.core.extensions.EMPTY
import java.net.URI

/**
 * @since 1.0.0
 */
public typealias Hostname = String

/**
 * @since 1.0.0
 */
public typealias FileSuffix = String

/**
 * @since 1.0.0
 */
public typealias AnimeId = String

/**
 * Configuration for a specific meta data provider. A meta data provider is a website providing data about anime such as myanimelist.net, kitsu.io or notify.moe
 * @since 1.0.0
 */
public interface MetaDataProviderConfig {

    /**
     * Distinguish between a test context and production context
     * @since 1.0.0
     * @return **true** if the current context is a test context. This is the case for unit tests for example.
     */
    public fun isTestContext(): Boolean = false

    /**
     * Hostname of the meta data provider. **Example:** `notify.moe`
     * @since 1.0.0
     * @return Hostname without protocol or leading www
     */
    public fun hostname(): Hostname

    /**
     * Builds the [URI] which is a direct link to an anime on the mata data provider's website
     * @param id Id of the anime on the meta data provider's website
     * @return Direct link to the anime on the website of the meta data provider
     */
    public fun buildAnimeLink(id: AnimeId): URI = URI("https://${hostname()}/anime/$id")

    /**
     * Builds the [URI] which is used to download data.
     * This can be the same as the [URI] created in [buildAnimeLink] or in case of GraphQL, for example, a totally different [URI].
     * @since 1.0.0
     * @param id Identifier within the [URI]. **Default** is an empty [String]
     * @return [URI] from which anime data can be downloaded.
     */
    public fun buildDataDownloadLink(id: String = EMPTY): URI = buildAnimeLink(id)

    /**
     * Extracts the [AnimeId] from a given [URI].
     * @since 2.1.0
     * @param uri Anime link uri. Could've been previously created using [buildAnimeLink]
     * @return The ID for the anime.
     * @throws IllegalArgumentException if the given [URI] does not contain the hostname.
     */
    public fun extractAnimeId(uri: URI): AnimeId {
        require(uri.toString().contains(hostname())) { "URI doesn't contain hostname [${hostname()}]" }
        return uri.toString().replace(buildAnimeLink(EMPTY).toString(), EMPTY)
    }

    /**
     * File suffix for the file in which the raw data is saved. No leading dot. **Example:** `html`
     * @since 1.0.0
     * @return File suffix without a leading dot
     */
    public fun fileSuffix(): FileSuffix
}