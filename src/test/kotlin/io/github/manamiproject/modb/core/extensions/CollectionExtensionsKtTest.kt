package io.github.manamiproject.modb.core.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import org.junit.jupiter.api.assertThrows

internal class CollectionExtensionsKtTest {

    @Nested
    inner class PickRandomElementTests {

        @Test
        fun `throw an error if the list is empty and pickRandom() is called`() {
            // when
            val result = assertThrows<IllegalStateException> {
                emptyList<String>().pickRandom()
            }

            // then
            assertThat(result).hasMessage("Cannot pick random element from empty list.")
        }

        @Test
        fun `always return the first element if the list contains exactly one element`() {
            // given
            val list = listOf("one")

            // when
            val result = list.pickRandom()

            assertThat(result).isEqualTo(list.first())
        }

        @Test
        fun `pick random element`() {
            // given
            val list = (1..25).toList()

            // when
            val result = listOf(list.pickRandom(), list.pickRandom(), list.pickRandom(), list.pickRandom())

            // then
            assertThat(list).containsAll(result)

            val firstElementDiffers = result[0] != result[1] || result[0] != result[2] || result[0] != result[3]
            val secondElementDiffers = result[1] != result[2] || result[1] != result[3]
            val thirdElementDiffers = result[2] != result[3]
            val elementsAreNotAllTheSame = firstElementDiffers || secondElementDiffers || thirdElementDiffers
            assertThat(elementsAreNotAllTheSame).isTrue()
        }
    }
}