package io.github.manamiproject.modb.core.extractor

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


public data object NotFound

public class ExtractionResult(private val delegate: Map<OutputKey, Any>) : Map<OutputKey, Any> by delegate {

    public fun notFound(identifier: OutputKey): Boolean = !delegate.containsKey(identifier) || delegate[identifier] == NotFound

    public fun isOfType(identifier: OutputKey, cl: KClass<*>): Boolean {
        check(delegate.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }
        return delegate[identifier]!!::class == cl
    }

    public fun string(identifier: OutputKey): String {
        check(delegate.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }
        return  delegate[identifier].toString()
    }

    public fun int(identifier: OutputKey): Int {
        check(delegate.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }

        val value = delegate[identifier]!!

        if (value is Number || value::class.isSubclassOf(Number::class)) {
            return (value as Number).toInt()
        }

        val convertedValue = value.toString().toIntOrNull()

        if (convertedValue != null) {
            return convertedValue
        }

        throw IllegalStateException("Unable to return value [$value] as Int.")
    }

    public fun double(identifier: OutputKey): Double {
        check(delegate.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }

        val value = delegate[identifier]!!

        if (value is Number || value::class.isSubclassOf(Number::class)) {
            return (value as Number).toDouble()
        }

        val convertedValue = value.toString().toDoubleOrNull()

        if (convertedValue != null) {
            return convertedValue
        }

        throw IllegalStateException("Unable to return value [$value] as Double.")
    }

    public inline fun <reified T> list(identifier: OutputKey, extractionResult: ExtractionResult = this): List<T> {
        check(extractionResult.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }

        val value = extractionResult[identifier]!!

        val list = if (value::class.isSubclassOf(Iterable::class)) {
            (value as Iterable<*>).toList()
        } else {
            listOf(value)
        }

        if (list.isEmpty()) {
            return emptyList()
        }

        val first = list.first()!!
        if (first !is T) {
            throw IllegalStateException("List elements are not if type [${T::class.qualifiedName}], but of type [${first::class.qualifiedName}].")
        }

        return list.map { it as T }
    }

    public inline fun <reified T> list(identifier: OutputKey, extractionResult: ExtractionResult = this, transform: (String) -> T): List<T> {
        check(extractionResult.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }

        val value = extractionResult[identifier]!!

        val list = if (value::class.isSubclassOf(Iterable::class)) {
            (value as Iterable<*>).toList()
        } else {
            listOf(value)
        }

        return list.map { transform.invoke(it.toString()) }
    }

    override fun toString(): String = delegate.map { "${it.key} => ${it.value}" }.joinToString("\n")

    override fun equals(other: Any?): Boolean = delegate == other

    override fun hashCode(): Int = delegate.hashCode()
}