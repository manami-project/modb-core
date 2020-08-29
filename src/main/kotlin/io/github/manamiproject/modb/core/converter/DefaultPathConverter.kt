package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.Anime
import java.nio.file.Path
import kotlin.streams.toList

/**
 * Uses an [AnimeConverter] to convert files and directories to [Anime]s
 * @since 1.0.0
 * @param animeConverter Converter for the raw content
 * @param fileSuffix File suffix to determine which files to include
 */
class DefaultPathConverter(
    private val animeConverter: AnimeConverter,
    private val fileSuffix: FileSuffix
) : PathConverter {

    override fun convert(path: Path): List<Anime> {
        return when{
            path.regularFileExists() -> convertSingleFile(path)
            path.directoryExists() -> convertAllFilesInADirectory(path)
            else -> throw IllegalArgumentException("Given path [$path] does not exist.")
        }
    }

    private fun convertSingleFile(file: RegularFile) = listOf(animeConverter.convert(file.readFile()))

    private fun convertAllFilesInADirectory(path: Directory): List<Anime> {
        return path.list()
            .filter { it.regularFileExists() }
            .filter { it.fileSuffix() == fileSuffix }
            .map { convert(it).first() }
            .toList()
    }
}