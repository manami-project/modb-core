package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.core.extensions.EMPTY
import java.net.URL

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
     * Builds the [URL] which is a direct link to an anime on the mata data provider's website
     * @param id Id of the anime on the meta data provider's website
     * @return Direct link to the anime on the website of the meta data provider
     */
    public fun buildAnimeLinkUrl(id: AnimeId): URL = URL("https://${hostname()}/anime/$id")

    /**
     * Builds the [URL] which is used to download data.
     * This can be the same as the [URL] created in [buildAnimeLinkUrl] or in case of GraphQL, for example, a totally different [URL].
     * @since 1.0.0
     * @param id Identifier within the [URL]. **Default** is an empty [String]
     * @return [URL] from which anime data can be downloaded.
     */
    public fun buildDataDownloadUrl(id: String = EMPTY): URL = buildAnimeLinkUrl(id)

    /**
     * Extracts the [AnimeId] from a given [URL].
     * @since 2.1.0
     * @param url Anime link url. Could've been previously created using [buildAnimeLinkUrl]
     * @return The ID for the anime.
     * @throws IllegalArgumentException if the given URL does not contain the hostname.
     */
    public fun extractAnimeId(url: URL): AnimeId {
        require(url.toString().contains(hostname())) { "URL doesn't contain hostname [${hostname()}]" }
        return url.toString().replace(buildAnimeLinkUrl(EMPTY).toString(), EMPTY)
    }

    /**
     * File suffix for the file in which the raw data is saved. No leading dot. **Example:** `html`
     * @since 1.0.0
     * @return File suffix without a leading dot
     */
    public fun fileSuffix(): FileSuffix
}