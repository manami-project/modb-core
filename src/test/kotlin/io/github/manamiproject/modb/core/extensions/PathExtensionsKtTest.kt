package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.test.tempDirectory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.*
import kotlin.streams.toList

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
    inner class ExistsTests {

        @Test
        fun `returns false if the given path does not exist`() {
            tempDirectory {
                // given
                val nonExistingPath = tempDir.resolve("non-existing-path")

                // when
                val result = nonExistingPath.exists()

                // then
                assertThat(result).isFalse()
            }
        }

        @Test
        fun `returns true if the given path exists`() {
            tempDirectory {
                // given
                val existingPath = tempDir

                // when
                val result = existingPath.exists()

                // then
                assertThat(result).isTrue()
            }
        }
    }

    @Nested
    inner class NotExistsTests {

        @Test
        fun `returns true if the given path does not exist`() {
            tempDirectory {
                // given
                val nonExistingPath = tempDir.resolve("non-existing-path")

                // when
                val result = nonExistingPath.notExists()

                // then
                assertThat(result).isTrue()
            }
        }

        @Test
        fun `returns false if the given path exists`() {
            tempDirectory {
                // given
                val existingPath = tempDir

                // when
                val result = existingPath.notExists()

                // then
                assertThat(result).isFalse()
            }
        }
    }

    @Nested
    inner class CreateFileTests {

        @Test
        fun `creates a path as file`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.json")

                // when
                val result = file.createFile()

                // then
                assertThat(result).exists()
                assertThat(result).isRegularFile()
            }
        }

        @Test
        fun `throws an exception if the file already exists`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.json").apply {
                    Files.createFile(this)
                }

                // when
                val result = assertThrows<FileAlreadyExistsException> {
                    file.createFile()
                }

                // then
                assertThat(result).hasMessage(file.toString())
            }
        }
    }

    @Nested
    inner class CreateDirectoryTests {

        @Test
        fun `creates a path as directroy`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test")

                // when
                val result = file.createDirectory()

                // then
                assertThat(result).exists()
                assertThat(result).isDirectory()
            }
        }

        @Test
        fun `throws an exception if the directroy already exists`() {
            tempDirectory {
                // given
                val directory = tempDir.resolve("test").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = assertThrows<FileAlreadyExistsException> {
                    directory.createDirectory()
                }

                // then
                assertThat(result).hasMessage(directory.toString())
            }
        }
    }

    @Nested
    inner class DeleteIfExistsTests {

        @Test
        fun `returns true if the given file existed and deletion was successful`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.json").apply {
                    Files.createFile(this)
                }

                // when
                val result = file.deleteIfExists()

                // then
                assertThat(result).isTrue()
                assertThat(file).doesNotExist()
            }
        }

        @Test
        fun `returns false if the given path does not exist and therefore deletion was unsuccessful`() {
            tempDirectory {
                // given
                val nonExistingPath = tempDir.resolve("non-existing-path")

                // when
                val result = nonExistingPath.deleteIfExists()

                // then
                assertThat(result).isFalse()
            }
        }

        @Test
        fun `returns true if the given directory existed and deletion was successful`() {
            tempDirectory {
                // given
                val directory = tempDir.resolve("test").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = directory.deleteIfExists()

                // then
                assertThat(result).isTrue()
                assertThat(directory).doesNotExist()
            }
        }

        @Test
        fun `throws an exception if the given directory exists, but is not empty and therefore deletion was unsuccessful`() {
            tempDirectory {
                // given
                val directory = tempDir.resolve("test").apply {
                    createDirectory()
                }

                val fileInsideDir = directory.resolve("file.txt").createFile()

                // when
                assertThrows<DirectoryNotEmptyException> {
                    directory.deleteIfExists()
                }

                // then
                assertThat(directory).exists()
                assertThat(fileInsideDir).exists()
            }
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
    inner class ListTests {

        @Test
        fun `throws an exception if the path is not a directory`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt").apply {
                    Files.createFile(this)
                }

                // when
                val result = assertThrows<NotDirectoryException> {
                    file.list()
                }

                // then
                assertThat(result).hasMessage(file.toString())
            }
        }

        @Test
        fun `lists all files and directories`() {
            tempDirectory {
                // given
                val file1 = tempDir.resolve("test.txt").apply {
                    Files.createFile(this)
                }

                val file2 = tempDir.resolve(".gitignore").apply {
                    Files.createFile(this)
                }

                val directory = tempDir.resolve("directory").apply {
                    Files.createDirectory(this)
                }

                // when
                val result = tempDir.list().toList()

                // then
                assertThat(result).containsExactlyInAnyOrder(file1, file2, directory)
            }
        }
    }

    @Nested
    inner class ReadAllLinesTests {

        @Test
        fun `throws exception if the path is not a regular file`() {
            tempDirectory {
                // when
                val result = assertThrows<NoSuchFileException> {
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
                val result = file.readAllLines()

                // then
                assertThat(result).containsExactly("This file", "  uses", "carriage return line feed [CRLF]")
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
                val result = file.readAllLines()

                // then
                assertThat(result).containsExactly("This file", "  uses", "line feed [LF]")
            }
        }
    }

    @Nested
    inner class ReadFileTests {

        @Test
        fun `throws exception if the path is not a regular file`() {
            tempDirectory {
                // when
                val result = assertThrows<NoSuchFileException> {
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
                val result = file.readFile()

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
                val result = file.readFile()

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
                val result = srcFile.copyTo(target)

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
                val result = srcFile.copyTo(target.resolve(expectedFileName))

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
                val result = assertThrows<FileAlreadyExistsException> {
                    srcFile.copyTo(target)
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
                val result = srcDirectory.copyTo(targetDirectory)

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
                val result = srcDirectory.copyTo(targetDirectory.resolve("src"))

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
                val result = assertThrows<FileAlreadyExistsException> {
                    srcDirectory.copyTo(targetDirectory)
                }

                // then
                assertThat(result).hasMessage(targetDirectory.resolve(srcDirectory.fileName).toString())
            }
        }
    }

    @Nested
    inner class WriteTests {

        @Test
        fun `write string to non-existing file`() {
            tempDirectory {
                // given
                val text = "This is\n  some text."
                val file = Files.createFile(tempDir.resolve("test.txt"))

                // when
                file.write(text)

                // then
                val lines = Files.readAllLines(file)
                assertThat(lines).hasSize(2)
                assertThat(lines[0]).isEqualTo("This is")
                assertThat(lines[1]).isEqualTo("  some text.")
            }
        }

        @Test
        fun `overrides the content of an existing file`() {
            tempDirectory {
                // given
                val text = "This is\n  some text."
                val file = Files.createFile(tempDir.resolve("test.txt")).apply {
                    Files.write(this, "Initial text".toByteArray())
                }

                // when
                file.write(text)

                // then
                val lines = Files.readAllLines(file)
                assertThat(lines).hasSize(2)
                assertThat(lines[0]).isEqualTo("This is")
                assertThat(lines[1]).isEqualTo("  some text.")
            }
        }
    }

    @Nested
    inner class CreateDirectoriesTests {

        @Test
        fun `create nested structure`() {
            tempDirectory {
                // given
                val path = tempDir.resolve("test").resolve("other")

                // when
                path.createDirectories()

                // then
                assertThat(tempDir.resolve("test")).exists()
                assertThat(tempDir.resolve("test")).isDirectory()
                assertThat(tempDir.resolve("test").resolve("other")).exists()
                assertThat(tempDir.resolve("test").resolve("other")).isDirectory()
            }
        }
    }

    @Nested
    inner class NewInputStreamTests {

        @Test
        fun `throws exception trying to create an InputStream on a non-existent Path`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt")

                // when
                val result = assertThrows<NoSuchFileException> {
                    file.newInputStream()
                }

                // then
                assertThat(result).hasMessage(file.toString())
            }
        }

        @Test
        fun `successfully creates a valid InputStream for an existent Path`() {
            tempDirectory {
                // given
                val text = "Contains some text."
                val file = Files.createFile(tempDir.resolve("test.txt")).apply {
                    Files.write(this, text.toByteArray())
                }

                // when
                val result = file.newInputStream()

                // then
                assertThat(result).hasContent(text)
            }
        }
    }

    @Nested
    inner class NewOutputStreamTests {

        @Test
        fun `successfully creates a new OutputStream for a non-existent Path`() {
            tempDirectory {
                // given
                val file = tempDir.resolve("test.txt")

                // when
                val result = file.newOutputStream()

                // then
                result.write("test")
                assertThat(Files.readString(file)).isEqualTo("test")
            }
        }

        @Test
        fun `successfully creates a valid OutputStream for an existent Path`() {
            tempDirectory {
                // given
                val text = "Contains some text."
                val file = Files.createFile(tempDir.resolve("test.txt")).apply {
                    Files.write(this, text.toByteArray())
                }

                // when
                val result = file.newOutputStream()

                // then
                result.write("test")
                assertThat(Files.readString(file)).isEqualTo("test")
            }
        }
    }
}
