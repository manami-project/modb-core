package io.github.manamiproject.modb.core.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ListExtensionsKtTest {

    @Nested
    inner class ShuffleTests {

        @Test
        fun `create a shuffled list`() {
            // given
            val sortedList = mutableListOf("A", "B", "C", "D")

            // when
            val result = sortedList.createShuffledList()

            // then
            assertThat(result).containsAll(sortedList)
            assertThat(result).doesNotContainSequence(sortedList)
        }

        @Test
        fun `list having only one element`() {
            // given
            val sortedList = mutableListOf("A")

            // when
            val result = sortedList.createShuffledList()

            // then
            assertThat(result).containsExactly("A")
        }

        @Test
        fun `empty list`() {
            // given
            val sortedList = emptyList<String>()

            // when
            val result = sortedList.createShuffledList()

            // then
            assertThat(result).isEmpty()
        }
    }

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
            val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)

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

    @Nested
    inner class ContainsExactlyInTheSameOrderTests {

        @Test
        fun `two lists with different size are not equal`() {
            // given
            val list = listOf("one")
            val otherList = listOf("one", "two")

            // when
            val result = list.containsExactlyInTheSameOrder(otherList)

            assertThat(result).isFalse()
        }

        @Test
        fun `two lists with the same element, but different order are not equal`() {
            // given
            val list = listOf("two", "one")
            val otherList = listOf("one", "two")

            // when
            val result = list.containsExactlyInTheSameOrder(otherList)

            assertThat(result).isFalse()
        }

        @Test
        fun `two lists with the same elements in the same order are equal`() {
            // given
            val list = listOf("one", "two")
            val otherList = listOf("one", "two")

            // when
            val result = list.containsExactlyInTheSameOrder(otherList)

            assertThat(result).isTrue()
        }
    }
}