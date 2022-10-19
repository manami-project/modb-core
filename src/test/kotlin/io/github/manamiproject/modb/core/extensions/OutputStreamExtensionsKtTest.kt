package io.github.manamiproject.modb.core.extensions

import io.github.manamiproject.modb.test.tempDirectory
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.outputStream

internal class OutputStreamExtensionsKtTest {

    @Test
    fun `directly call write for a String on the stream`() {
        tempDirectory {
            // given
            val file = tempDir.resolve("file.txt")
            val outputStream = file.outputStream()
            val content = "Test"

            // when
            outputStream.write(content)

            // then
            val fileContent = runBlocking { file.readFileSuspendable() }
            assertThat(fileContent).isEqualTo(content)
        }
    }
}