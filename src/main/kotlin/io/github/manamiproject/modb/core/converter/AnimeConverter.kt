package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.models.Anime

/**
 * Converts raw content in form of a [String] into an [Anime]
 * @since 1.0.0
 */
interface AnimeConverter {

    /**
     * Converts a [String] into an [Anime].
     * @since 1.0.0
     * @param source Raw content
     * @return Instance of [Anime]
     */
    fun convert(source: String): Anime
}