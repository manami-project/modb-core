package io.github.manamiproject.modb.core.extractor

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * @since 12.0.0
 */
public data object NotFound


/**
 * Result of a [DataExtractor].
 * @since 12.0.0
 * @param delegate The map which acts as base for this implementation of [Map].
 */
public class ExtractionResult(private val delegate: Map<OutputKey, Any> = emptyMap()) : Map<OutputKey, Any> by delegate {

    /**
     * @since 12.0.0
     * @param identifier The output key to check.
     * @return True if the key exsists in the map and is not of type [NotFound].
     */
    public fun notFound(identifier: OutputKey): Boolean = !delegate.containsKey(identifier) || delegate[identifier] == NotFound

    /**
     * @since 12.0.0
     * @param identifier The output key to check.
     * @param cl Type to check.
     * @return True if the [identifier] is of type [cl]. It won't return true for a subtype.
     */
    public fun isOfType(identifier: OutputKey, cl: KClass<*>): Boolean {
        check(delegate.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }
        return delegate[identifier]!!::class == cl
    }

    /**
     * @param 12.0.0
     * @param identifier The output key for the value to retrieve.
     * @return Value of the entry with key [identifier] as [String].
     * @throws IllegalStateException If [identifier] doesn't exist.
     */
    public fun string(identifier: OutputKey): String {
        check(delegate.containsKey(identifier)) { "Result doesn't contain entry [$identifier]" }
        return  delegate[identifier].toString()
    }

    /**
     * Can return values as [Int]. Values can be any implementation of [Number].
     * Casting [String]s is also possible, but can alter results. Example `"054"` would be cast to `54`.
     * @since 12.0.0
     * @param identifier The output key for the value to retrieve.
     * @return Value of the entry with key [identifier] as [Int].
     * @throws IllegalStateException If [identifier] doesn't exist or casting to [Int] is not possible.
     */
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

    /**
     * Can return values as [Double]. Values can be any implementation of [Number].
     * Casting [String]s is also possible, but can alter results. Example `"054"` would be cast to `54.0`.
     * @since 12.0.0
     * @param identifier The output key for the value to retrieve.
     * @return Value of the entry with key [identifier] as [Double].
     * @throws IllegalStateException If [identifier] doesn't exist or casting to [Double] is not possible.
     */
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

    /**
     * Can return values as [List]. Single values will be wrapped in list.
     * @since 12.0.0
     * @param identifier The output key for the value to retrieve.
     * @param extractionResult Parameter required by inline function. Normally you don't need to set this.
     * @return Value of the entry with key [identifier] as [List] of type [T].
     * @throws IllegalStateException If [identifier] doesn't exist or not all elements are of type [T].
     */
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

        if (!list.all { it is T }) {
            throw IllegalStateException("List not all elements are of type [${T::class.qualifiedName}].")
        }

        return list.map { it as T }
    }

    /**
     * Same as [list], but allows add a transformator which allows to transform elements from [String] to [T].
     *
     * @since 12.0.0
     * @param identifier The output key for the value to retrieve.
     * @param extractionResult Parameter required by inline function. Normally you don't need to set this.
     * @param transform Allows to transform the elements from [String] to [T].
     * @return Value of the entry with key [identifier] as [List] of type [T].
     * @throws IllegalStateException If [identifier] doesn't exist.
     * @see [list]
     */
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