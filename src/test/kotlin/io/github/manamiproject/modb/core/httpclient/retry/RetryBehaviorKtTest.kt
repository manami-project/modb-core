package io.github.manamiproject.modb.core.httpclient.retry

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RetryBehaviorKtTest {

    @Nested
    inner class AddExecuteBeforeRetryPredicateTests {

        @Test
        fun `returns true if a new executeBeforeRetryPredicate has been added successfully`() {
            // given
            val retryBehavior = RetryBehavior(retryOnResponsePredicate = { false })

            // when
            val result = retryBehavior.addExecuteBeforeRetryPredicate(403) { println("test") }

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns false if an executeBeforeRetryPredicate wasn't added, because an entry for the given HttpResponseCode already exists`() {
            // given
            val retryBehavior = RetryBehavior(retryOnResponsePredicate = { false }).apply {
                addExecuteBeforeRetryPredicate(403) { println("test") }
            }

            // when
            val result = retryBehavior.addExecuteBeforeRetryPredicate(403) { println("other") }

            // then
            assertThat(result).isFalse()
        }
    }
}