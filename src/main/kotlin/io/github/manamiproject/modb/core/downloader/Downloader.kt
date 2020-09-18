package io.github.manamiproject.modb.core.downloader

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.models.Anime

/**
 * Downloads raw content containing data which describes an anime
 * @since 1.0.0
 */
public interface Downloader {

    /**
     * Downloads raw data for a specific anime which then can be converted into an [Anime] using an [AnimeConverter]
     * @since 1.0.0
     * @param id The id of the anime for which you want to download the raw data
     * @param onDeadEntry Function that is executed if the anime in question is found to be a dead entry on the website
     * of the metadata provider when it is downloaded. **Default:** is no action
     * @return Raw data
     */
    public fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit = {}): String
}