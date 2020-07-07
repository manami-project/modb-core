package io.github.manamiproject.modb.core.logging

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LoggerDelegateTest {

    private val logger by LoggerDelegate()

    @Test
    fun `name of the logger is the full qualified path of the class instantiating the delegate`() {
        assertThat(logger.name).isEqualTo("io.github.manamiproject.modb.core.logging.LoggerDelegateTest")
    }
}