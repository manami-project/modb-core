package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.tempDirectory
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

internal class PathExtensionsKtTest {

    @Nested
    inner class ChangeSuffixTests {

        @Test
        fun `does nothing if the given path is a directory`() {
            tempDirectory {
                // given
                val directory = tempDir.resolve("test").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = directory.changeSuffix(LOCK_FILE_SUFFIX)

                // then
                assertThat(result).isEqualTo(directory)
            }
        }

        @Test
        fun `Changes suffix which starts with a dot`() {
            tempDirectory {
                // given
                val originalFile = tempDir.resolve("file.json").apply {
                    Files.createFile(this)
                }

                // when
                originalFile.changeSuffix(LOCK_FILE_SUFFIX).apply {
                    Files.createFile(this)
                }

                // then
                assertThat(tempDir.resolve("file.$LOCK_FILE_SUFFIX")).exists()
            }
        }

        @Test
        fun `Changes suffix which doesn't start with a dot`() {
            tempDirectory {
                // given
                val originalFile = tempDir.resolve("file.json").apply {
                    Files.createFile(this)
                }

                // when
                originalFile.changeSuffix(LOCK_FILE_SUFFIX).apply {
                    Files.createFile(this)
                }

                // then
                assertThat(tempDir.resolve("file.$LOCK_FILE_SUFFIX")).exists()
            }
        }

        @Test
        fun `Correctly changes only the part after the last dot`() {
            tempDirectory {
                // given
                val originalFile = tempDir.resolve("file.json.BAK").apply {
                    Files.createFile(this)
                }

                // when
                originalFile.changeSuffix(EMPTY).apply {
                    Files.createFile(this)
                }

                // then
                assertThat(tempDir.resolve("file.json")).exists()
            }
        }

        @Test
        fun `Add suffix to file without suffix`() {
            tempDirectory {
                // given
                val originalFile = tempDir.resolve("config").apply {
                    Files.createFile(this)
                }

                // when
                originalFile.changeSuffix(".BAK").apply {
                    Files.createFile(this)
                }

                // then
                assertThat(tempDir.resolve("config.BAK")).exists()
            }
        }

        @Test
        fun `Add suffix to 'hidden' files starting with a dot`() {
            tempDirectory {
                // given
                val originalFile = tempDir.resolve(".gitignore").apply {
                    Files.createFile(this)
                }

                // when
                originalFile.changeSuffix(".BAK").apply {
                    Files.createFile(this)
                }

                // then
                assertThat(tempDir.resolve(".gitignore.BAK")).exists()
            }
        }

        @Test
        fun `File without parent directory`() {
            // given
            val originalFile = Paths.get("test.html")

            // when
            val newFile = originalFile.changeSuffix(LOCK_FILE_SUFFIX)


            // then
            assertThat(newFile.fileName.toString()).isEqualTo("test.$LOCK_FILE_SUFFIX")
        }
    }

    @Nested
    inner class RegularFileExists {

        @Test
        fun `returns false if the given path does not exist`() {
            tempDirectory {
                // given
                val nonExistingFile = tempDir.resolve("non-existing-path.json")

                // when
                val result = nonExistingFile.regularFileExists()

                // then
                assertThat(result).isFalse()
            }
        }

        @Test
        fun `returns false if the given path exist, but is not a regular file`() {
            tempDirectory {
                // given
                val existingPath = tempDir.resolve("test").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = existingPath.regularFileExists()

                // then
                assertThat(result).isFalse()
            }
        }

        @Test
        fun `returns true if the given path exists and is a regular file`() {
            tempDirectory {
                // given
                val existingPath = tempDir.resolve("test.json").apply {
                    Files.createFile(this)
                }

                // when
                val result = existingPath.regularFileExists()

                // then
                assertThat(result).isTrue()
            }
        }
    }

    @Nested
    inner class DirectoryExists {

        @Test
        fun `returns false if the given path does not exist`() {
            tempDirectory {
                // given
                val nonExistingDirectory = tempDir.resolve("non-existing-path")

                // when
                val result = nonExistingDirectory.directoryExists()

                // then
                assertThat(result).isFalse()
            }
        }

        @Test
        fun `returns false if the given path exist, but is not a directory`() {
            tempDirectory {
                // given
                val existingPath = tempDir.resolve("test.json").apply {
                    Files.createFile(this)
                }

                // when
                val result = existingPath.directoryExists()

                // then
                assertThat(result).isFalse()
            }
        }

        @Test
        fun `returns true if the given path exists and is a directory`() {
            tempDirectory {
                // given
                val existingPath = tempDir.resolve("test").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = existingPath.directoryExists()

                // then
                assertThat(result).isTrue()
            }
        }
    }

    @Nested
    inner class ReadFileTests {

        @Test
        fun `throws exception if the path is not a regular file`() {
            tempDirectory {
                // when
                val result = exceptionExpected<NoSuchFileException> {
                    tempDir.readFile()
                }

                // then
                assertThat(result)
            }
        }

        @Test
        fun `correctly reads file using carriage return line feed`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt").apply {
                    Files.createFile(this)
                    Files.write(this, "This file\r\n  uses\r\ncarriage return line feed [CRLF]".toByteArray())
                }

                // when
                val result = runBlocking { file.readFile() }

                // then
                assertThat(result).isEqualTo("""This file
  uses
carriage return line feed [CRLF]""")
            }
        }

        @Test
        fun `correctly reads file using line feed`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt").apply {
                    Files.createFile(this)
                    Files.write(this, "This file\n  uses\nline feed [LF]".toByteArray())
                }

                // when
                val result = runBlocking { file.readFile() }

                // then
                assertThat(result).isEqualTo("""This file
  uses
line feed [LF]""")
            }
        }
    }

    @Nested
    inner class CopyTests {

        @Test
        fun `successfully copy a single file by passing an existing directory`() {
            tempDirectory {
                // given
                val fileName = "test.txt"

                val srcFile = Files.createFile(tempDir.resolve(fileName)).apply {
                    Files.write(this, "File content.".toByteArray())
                }

                val target = tempDir.resolve("target").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = runBlocking { srcFile.copyToSuspedable(target) }

                // then
                assertThat(tempDir.resolve("target").resolve(fileName)).exists()
                assertThat(Files.readString(result)).isEqualTo(Files.readString(srcFile))
            }
        }

        @Test
        fun `successfully copy a single file by passing a non-existing path`() {
            tempDirectory {
                // given
                val fileName = "test.txt"
                val expectedFileName = "other.txt"

                val srcFile = Files.createFile(tempDir.resolve(fileName)).apply {
                    Files.write(this, "File content.".toByteArray())
                }

                val target = tempDir.resolve("target").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = runBlocking { srcFile.copyToSuspedable(target.resolve(expectedFileName)) }

                // then
                assertThat(target.resolve(expectedFileName)).exists()
                assertThat(Files.readString(result)).isEqualTo(Files.readString(srcFile))
            }
        }

        @Test
        fun `throws an exception if the file already exists`() {
            tempDirectory {
                // given
                val fileName = "test.txt"

                val srcFile = Files.createFile(tempDir.resolve(fileName)).apply {
                    Files.write(this, "File content.".toByteArray())
                }

                val target = tempDir.resolve("target").apply {
                    Files.createDirectory(this)
                    Files.createFile(this.resolve(fileName))
                }

                // when
                val result = exceptionExpected<FileAlreadyExistsException> {
                    srcFile.copyToSuspedable(target)
                }

                // then
                assertThat(result).hasMessage(target.resolve(fileName).toString())
            }
        }

        @Test
        fun `successfully copy a directory into another existing directory`() {
            tempDirectory {
                // given
                val srcDirectory = tempDir.resolve("src").apply {
                    Files.createDirectory(this)
                }

                val targetDirectory = tempDir.resolve("target").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = runBlocking { srcDirectory.copyToSuspedable(targetDirectory) }

                // then
                assertThat(tempDir.resolve("src")).exists()
                assertThat(result).exists()
                assertThat(result).isEqualTo(targetDirectory.resolve("src"))
            }
        }

        @Test
        fun `successfully copy a directory by passing a non-existing path`() {
            tempDirectory {
                // given
                val srcDirectory = tempDir.resolve("src").apply {
                    Files.createDirectory(this)
                }

                val targetDirectory = tempDir.resolve("target").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = runBlocking { srcDirectory.copyToSuspedable(targetDirectory.resolve("src")) }

                // then
                assertThat(tempDir.resolve("src")).exists()
                assertThat(result).exists()
                assertThat(result).isEqualTo(targetDirectory.resolve("src"))
            }
        }

        @Test
        fun `throws an exception if the directory already exists`() {
            tempDirectory {
                // given
                val srcDirectory = tempDir.resolve("src").apply {
                    Files.createDirectory(this)
                }

                val targetDirectory = tempDir.resolve("target").apply {
                    Files.createDirectory(this)
                    Files.createDirectory(this.resolve("src"))
                }

                // when
                val result = exceptionExpected<FileAlreadyExistsException> {
                    srcDirectory.copyToSuspedable(targetDirectory)
                }

                // then
                assertThat(result).hasMessage(targetDirectory.resolve(srcDirectory.fileName).toString())
            }
        }
    }

    @Nested
    inner class FileNameTests {

        @Test
        fun `correctly return the file name`() {
            tempDirectory {
                // given
                val expectedName = "test.json"
                val file = tempDir.resolve(expectedName)
                Files.createFile(file)

                // when
                val result = file.fileName()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }

        @Test
        fun `correctly return the file name of a hidden file`() {
            tempDirectory {
                // given
                val expectedName = ".gitignore"
                val file = tempDir.resolve(expectedName)
                Files.createFile(file)

                // when
                val result = file.fileName()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }

        @Test
        fun `correctly return name even if the given path is a directory`() {
            tempDirectory {
                // given
                val expectedName = "subdir"
                val dir = tempDir.resolve(expectedName)
                Files.createDirectory(dir)

                // when
                val result = dir.fileName()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }
    }

    @Nested
    inner class FileSuffixTests {

        @Test
        fun `correctly return the file suffix`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.json")
                Files.createFile(file)

                // when
                val result = file.fileSuffix()

                // then
                assertThat(result).isEqualTo("json")
            }
        }

        @Test
        fun `return full name if the file is a hidden file`() {
            tempDirectory {
                // given
                val expectedName = ".gitignore"
                val file = tempDir.resolve(expectedName)
                Files.createFile(file)

                // when
                val result = file.fileSuffix()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }

        @Test
        fun `return full name if there is no dot followed by a file suffix`() {
            tempDirectory {
                // given
                val expectedName = "test"
                val file = tempDir.resolve(expectedName)
                Files.createFile(file)

                // when
                val result = file.fileSuffix()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }

        @Test
        fun `correctly return the file suffix even if the given path is a directory`() {
            tempDirectory {
                // given
                val expectedName = "subdir.more"
                val dir = tempDir.resolve(expectedName)
                Files.createDirectory(dir)

                // when
                val result = dir.fileSuffix()

                // then
                assertThat(result).isEqualTo("more")
            }
        }

        @Test
        fun `return full name if there is no dot followed by a file suffix even if the given path is a directory`() {
            tempDirectory {
                // given
                val expectedName = "subdir"
                val dir = tempDir.resolve(expectedName)
                Files.createDirectory(dir)

                // when
                val result = dir.fileSuffix()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }

        @Test
        fun `return full name if the given path is a hidden directory`() {
            tempDirectory {
                // given
                val expectedName = ".git"
                val dir = tempDir.resolve(expectedName)
                Files.createDirectory(dir)

                // when
                val result = dir.fileSuffix()

                // then
                assertThat(result).isEqualTo(expectedName)
            }
        }

        @Test
        fun `return correct suffix from a file having a dot within the name`() {
            tempDirectory {
                // given
                val dir = tempDir.resolve("a_name_with_version_2.x.xml")
                Files.createDirectory(dir)

                // when
                val result = dir.fileSuffix()

                // then
                assertThat(result).isEqualTo("xml")
            }
        }
    }
}
