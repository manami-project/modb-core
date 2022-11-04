package io.github.manamiproject.modb.core.converter

import io.github.manamiproject.modb.core.TestAnimeConverter
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import io.github.manamiproject.modb.test.tempDirectory
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile

internal class DefaultPathConverterTest {

    @Test
    fun `throws exception if the given Path does not exist`() {
        tempDirectory {
            // given
            val file = tempDir.resolve("test.txt")

            val specificTestConverter = object: AnimeConverter by TestAnimeConverter { }
            val converter = DefaultPathConverter(specificTestConverter, "txt")

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                converter.convert(file)
            }

            // then
            assertThat(result).hasMessage("Given path [$file] does not exist.")
        }
    }

    @Test
    fun `successfully converts a single file`() {
        tempDirectory {
            // given
            val file = tempDir.resolve("test.txt").createFile()
            runBlocking {
                "Correct file".writeToFile(file)
            }

            val expectedAnime =  Anime("Expected Anime")

            val specificTestConverter = object: AnimeConverter by TestAnimeConverter {
                override suspend fun convert(rawContent: String): Anime {
                    return if (rawContent == "Correct file") {
                        expectedAnime
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val converter = DefaultPathConverter(specificTestConverter, "txt")

            // when
            val result = runBlocking { converter.convert(file) }

            // then
            assertThat(result).containsExactly(expectedAnime)
        }
    }

    @Test
    fun `successfully convert a whole directory`() {
        tempDirectory {
            // given
            val directory = runBlocking {
                tempDir.resolve("dir").createDirectory().apply {
                    "accept 1".writeToFile(this.resolve("file1.txt"))
                    "accept 2".writeToFile(this.resolve("file2.txt"))
                }
            }

            val specificTestConverter = object: AnimeConverter by TestAnimeConverter {
                override suspend fun convert(rawContent: String): Anime {
                    return if (rawContent.startsWith("accept")) {
                        Anime(rawContent)
                    } else {
                        shouldNotBeInvoked()
                    }
                }
            }

            val converter = DefaultPathConverter(specificTestConverter, "txt")

            // when
            val result = runBlocking { converter.convert(directory) }

            // then
            assertThat(result).containsExactlyInAnyOrder(
                Anime("accept 1"),
                Anime("accept 2")
            )
        }
    }
}