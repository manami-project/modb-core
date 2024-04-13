package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.Anime
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.nio.file.Path

/**
 * @since 11.1.0
 */
@Deprecated(
    message = "Has been renamed to DefaultPathAnimeConverter. DefaultPathConverter will be removed in the next major version.",
    replaceWith = ReplaceWith(
        expression = "DefaultPathAnimeConverter",
        imports = [
            "io.github.manamiproject.modb.core.converter.DefaultPathAnimeConverter",
        ]
    )
)
public typealias DefaultPathConverter = DefaultPathAnimeConverter


/**
 * Uses an [AnimeConverter] to convert a single file or multiple files in a directory to [Anime]s.
 * @since 1.0.0
 * @param animeConverter Converter for the raw content.
 * @param fileSuffix File suffix to determine which files to include.
 */
public class DefaultPathAnimeConverter(
    private val animeConverter: AnimeConverter,
    private val fileSuffix: FileSuffix,
) : PathAnimeConverter {

    override suspend fun convert(path: Path): List<Anime> = withContext(LIMITED_CPU) {
        when{
            path.regularFileExists() -> convertSingleFile(path)
            path.directoryExists() -> convertAllFilesInADirectory(path)
            else -> throw IllegalArgumentException("Given path [$path] does not exist.")
        }
    }

    private suspend fun convertSingleFile(file: RegularFile) = withContext(LIMITED_CPU) {
        listOf(animeConverter.convert(file.readFile()))
    }

    private suspend fun convertAllFilesInADirectory(path: Directory): List<Anime> = withContext(LIMITED_FS) {
        val jobs = path.listRegularFiles(glob = "*$fileSuffix")
            .map {
                async {
                    convertSingleFile(it)
                }
            }

        awaitAll(*jobs.toTypedArray()).flatten().toList()
    }
}