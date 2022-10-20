package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.Anime
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.nio.file.Path
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

    @Deprecated("Use coroutines",
        ReplaceWith("runBlocking { convertSuspendable(path) }", "kotlinx.coroutines.runBlocking")
    )
    override fun convert(path: Path): List<Anime> {
        return runBlocking {
            convertSuspendable(path)
        }
    }

    override suspend fun convertSuspendable(path: Path): List<Anime> {
        return withContext(IO) {
            when{
                path.regularFileExists() -> convertSingleFile(path)
                path.directoryExists() -> convertAllFilesInADirectory(path)
                else -> throw IllegalArgumentException("Given path [$path] does not exist.")
            }
        }
    }

    private suspend fun convertSingleFile(file: RegularFile) = withContext(IO) {
        listOf(animeConverter.convertSuspendable(file.readFileSuspendable()))
    }

    private suspend fun convertAllFilesInADirectory(path: Directory): List<Anime> = withContext(IO) {
        path.useDirectoryEntries { pathSequence ->
            pathSequence.filter { it.regularFileExists() }
                .filter { it.fileSuffix() == fileSuffix }
                .map {
                    runBlocking {
                        yield()
                        convertSingleFile(it)
                    }
                }
                .flatten()
                .toList()
        }
    }
}