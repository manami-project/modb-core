package io.github.manamiproject.modb.core.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test

internal class SortedListTest {

    @Test
    fun `sort list if the list is initially added by using the constructor passing a mutable list`() {
        // given
        val list = mutableListOf(
                "Zankyou no Terror",
                "Gintama",
                "Natsume Yuujinchou Roku",
                "Ashita no Joe 2",
        )

        // when
        val result = SortedList(
                list = list,
        )

        // then
        assertThat(result.toList()).containsExactly(
                "Ashita no Joe 2",
                "Gintama",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror",
        )
    }

    @Test
    fun `sort list if the list is initially added by using the constructor`() {
        // when
        val result = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // then
        assertThat(result.toList()).containsExactly(
            "Ashita no Joe 2",
            "Gintama",
            "Natsume Yuujinchou Roku",
            "Zankyou no Terror",
        )
    }

    @Test
    fun `sort list descending if the list is initially added by using the constructor passing a mutable list and a specific comparator`() {
        // given
        val list = mutableListOf(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // when
        val result = SortedList(
            list = list,
            comparator = { o1, o2 -> o2.compareTo(o1) }
        )

        // then
        assertThat(result.toList()).containsExactly(
            "Zankyou no Terror",
            "Natsume Yuujinchou Roku",
            "Gintama",
            "Ashita no Joe 2",
        )
    }

    @Test
    fun `sort list when adding an element`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
        )

        // when
        list.add("Ashita no Joe 2")

        // then
        assertThat(list.toList()).containsExactly(
            "Ashita no Joe 2",
            "Gintama",
            "Natsume Yuujinchou Roku",
            "Zankyou no Terror"
        )
    }

    @Test
    fun `ignore selected index when using add with index and sort list`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
        )

        // when
        list.add(2, "Ashita no Joe 2")

        // then
        assertThat(list.toList()).containsExactly(
                "Ashita no Joe 2",
                "Gintama",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `sort list when adding a list of elements`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
        )

        // when
        list.addAll(listOf("Chihayafuru", "Ashita no Joe 2"))

        // then
        assertThat(list.toList()).containsExactly(
                "Ashita no Joe 2",
                "Chihayafuru",
                "Gintama",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `ignore selected index when using addAll with index and sort list`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
        )

        // when
        list.addAll(2, listOf("Chihayafuru", "Ashita no Joe 2"))

        // then
        assertThat(list.toList()).containsExactly(
                "Ashita no Joe 2",
                "Chihayafuru",
                "Gintama",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `guarantee sorted list when removing an element`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Natsume Yuujinchou Roku",
        )

        // when
        list.remove("Gintama")

        // then
        assertThat(list.toList()).containsExactly(
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `guarantee sorted list when removing a list of elements`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Chihayafuru",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // when
        list.removeAll(listOf("Gintama", "Ashita no Joe 2"))

        // then
        assertThat(list.toList()).containsExactly(
                "Chihayafuru",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `guarantee sorted list when removing an element at a specific position`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Chihayafuru",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // when
        list.removeAt(2)

        // then
        assertThat(list.toList()).containsExactly(
                "Ashita no Joe 2",
                "Chihayafuru",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `guarantee sorted list when removing an element using a predicate`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Chihayafuru",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // when
        list.removeIf { it.startsWith("G") }

        // then
        assertThat(list.toList()).containsExactly(
                "Ashita no Joe 2",
                "Chihayafuru",
                "Natsume Yuujinchou Roku",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `replace entry on specific position and sort list`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Chihayafuru",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // when
        list[2] = "Suzumiya Haruhi no Shoushitsu"

        // then
        assertThat(list.toList()).containsExactly(
                "Ashita no Joe 2",
                "Chihayafuru",
                "Natsume Yuujinchou Roku",
                "Suzumiya Haruhi no Shoushitsu",
                "Zankyou no Terror"
        )
    }

    @Test
    fun `guarantee sorted list when using retainAll`() {
        // given
        val list = SortedList(
            "Zankyou no Terror",
            "Gintama",
            "Chihayafuru",
            "Natsume Yuujinchou Roku",
            "Ashita no Joe 2",
        )

        // when
        list.retainAll(listOf("Natsume Yuujinchou Roku", "Chihayafuru"))

        // then
        assertThat(list.toList()).containsExactly(
                "Chihayafuru",
                "Natsume Yuujinchou Roku"
        )
    }

    @Test
    fun `toString() lists elements`() {
        // given
        val list = SortedList(
            "A",
            "B",
            "C",
        )

        // when
        val result = list.toString()

        // then
        assertThat(result).isEqualTo("[A, B, C]")
    }

    @Nested
    inner class EqualityTests {

        @Test
        fun `two lists are not equal having the same elements, but a different comparator and therefore a different order`() {
            // given
            val list1 = SortedList(
                    list = mutableListOf("A", "B", "C"),
                    comparator = { o1, o2 -> o1.compareTo(o2) }
            )

            val list2 = SortedList(
                    list = mutableListOf("C", "B", "A"),
                    comparator = { o1, o2 -> o1.compareTo(o2) * -1 }
            )

            // when
            val result = list1 == list2

            // then
            assertThat(result).isFalse()
            assertThat(list1).isNotEqualTo(list2)
        }

        @Test
        fun `two lists are equal having the same elements and the same comparator`() {
            // given
            val list1 = SortedList(
                "A",
                "B",
                "C",
            )

            val list2 = SortedList(
                "C",
                "B",
                "A",
            )

            // when
            val result = list1 == list2

            // then
            assertThat(result).isTrue()
            assertThat(list1).isEqualTo(list2)
        }

        @Test
        fun `two lists are not equal having different generics`() {
            // given
            val list1 = SortedList(
                "A",
                "B",
                "C",
            )

            val list2 = SortedList(
                    list = mutableListOf(1, 2, 3),
                    comparator = { o1, o2 -> o1.compareTo(o2) }
            )

            // when
            val result = list1.equals(list2)

            // then
            assertThat(result).isFalse()
            assertThat(list1).isNotEqualTo(list2)
        }

        @Test
        fun `two lists are not equal having different object type`() {
            // given
            val list1 = SortedList(
                "A",
                "B",
                "C",
            )

            // when
            val result = list1.equals(1)

            // then
            assertThat(result).isFalse()
        }
    }
}