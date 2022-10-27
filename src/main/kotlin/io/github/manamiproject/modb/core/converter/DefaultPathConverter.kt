package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.Anime
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.useDirectoryEntries

/**
 * Uses an [AnimeConverter] to convert files and directories to [Anime]s
 * @since 1.0.0
 * @param animeConverter Converter for the raw content
 * @param fileSuffix File suffix to determine which files to include
 */
public class DefaultPathConverter(
    private val animeConverter: AnimeConverter,
    private val fileSuffix: FileSuffix,
) : PathConverter {

    override suspend fun convertSuspendable(path: Path): List<Anime> = withContext(LIMITED_CPU) {
        when{
            path.regularFileExists() -> convertSingleFile(path)
            path.directoryExists() -> convertAllFilesInADirectory(path)
            else -> throw IllegalArgumentException("Given path [$path] does not exist.")
        }
    }

    private suspend fun convertSingleFile(file: RegularFile) = withContext(LIMITED_CPU) {
        listOf(animeConverter.convertSuspendable(file.readFileSuspendable()))
    }

    private suspend fun convertAllFilesInADirectory(path: Directory): List<Anime> = withContext(LIMITED_FS) {
        val jobs = path.listDirectoryEntries(glob = "*$fileSuffix")
            .filter { it.regularFileExists() }
            .map {
                async {
                    convertSingleFile(it)
                }
            }

        awaitAll(*jobs.toTypedArray()).flatten().toList()
    }
}