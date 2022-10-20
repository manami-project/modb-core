package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.models.Anime
import java.nio.file.Path

/**
 * Converts files and direcoories into a single [Anime] or a [List] of [Anime]s
 * @since 1.0.0
 */
public interface PathConverter {

    /**
     * Converts a file into a single [Anime] (wrapped in a [List]) or a directory into a [List] of [Anime]s.
     * @since 1.0.0
     * @param path Can either be a file or a directory
     * @return Converted [Anime]
     * @throws IllegalArgumentException if the given [Path] is neither file nor directory
     */
    @Deprecated("Use coroutines")
    public fun convert(path: Path): List<Anime>

    /**
     * Converts a file into a single [Anime] (wrapped in a [List]) or a directory into a [List] of [Anime]s.
     * @since 8.0.0
     * @param path Can either be a file or a directory
     * @return Converted [Anime]
     * @throws IllegalArgumentException if the given [Path] is neither file nor directory
     */
    public suspend fun convertSuspendable(path: Path): Collection<Anime>
}