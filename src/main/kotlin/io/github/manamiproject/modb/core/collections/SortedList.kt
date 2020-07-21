package io.github.manamiproject.modb.core.collections

import java.net.URL
import java.util.function.Predicate

internal class SortedList<T>(
        private val list: MutableList<T> = mutableListOf(),
        private val comparator: Comparator<T>
) : MutableList<T> by list {

    init {
        list.sortWith(comparator)
    }

    override fun add(element: T): Boolean {
        val hasBeenModified = list.add(element)
        list.sortWith(comparator)

        return hasBeenModified
    }

    override fun add(index: Int, element: T) {
        add(element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return addAll(elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val hasBeenModified = list.addAll(elements)
        list.sortWith(comparator)

        return hasBeenModified
    }

    override fun remove(element: T): Boolean {
        val hasBeenModified = list.remove(element)
        list.sortWith(comparator)

        return hasBeenModified
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val hasBeenModified = list.removeAll(elements)
        list.sortWith(comparator)

        return hasBeenModified
    }

    override fun removeAt(index: Int): T {
        val returnValue = list.removeAt(index)
        list.sortWith(comparator)

        return returnValue
    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        val hasBeenModified = list.removeIf(filter)
        list.sortWith(comparator)

        return hasBeenModified
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val hasBeenModified = list.retainAll(elements)
        list.sortWith(comparator)

        return hasBeenModified
    }

    override fun set(index: Int, element: T): T {
        val returnValue = list.set(index, element)
        sortWith(comparator)

        return returnValue
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SortedList<*>) return false
        if (other === this) return true

        return other.toList() == list.toList()
    }

    override fun hashCode(): Int {
        return list.toList().hashCode()
    }

    companion object {
        internal val URL_COMPARATOR = Comparator<URL> { o1, o2 -> o1.toString().compareTo(o2.toString()) }
        internal val STRING_COMPARATOR = Comparator<String> { o1, o2 -> o1.compareTo(o2) }
    }
}