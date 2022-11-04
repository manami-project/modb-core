package io.github.manamiproject.modb.core.collections

import java.util.Comparator
import java.util.function.Predicate

/**
 * This [List] automatically sorts itself on any modifying invocation.
 * @since 2.1.0
 * @property list Initial list of elements. **Default:** empty list
 * @property comparator Comparator used to sort the elements. **Default:** Simple call of `compareTo`
 */
public class SortedList<T: Comparable<T>>(
    private val list: MutableList<T> = mutableListOf(),
    private val comparator: Comparator<T> = Comparator { o1, o2 -> o1.compareTo(o2) },
) : MutableList<T> by list {

    /**
     * @param values Initial values
     * @since 3.1.0
     */
    public constructor(vararg values: T) : this(values.toMutableList())

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

    override fun toString(): String = list.toString()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SortedList<*>) return false
        if (other === this) return true

        return other.toList() == list.toList()
    }

    override fun hashCode(): Int {
        return list.toList().hashCode()
    }
}