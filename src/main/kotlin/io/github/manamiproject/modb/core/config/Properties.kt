package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.core.extensions.EMPTY
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.reflect.KProperty

public class StringPropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: String, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): String = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.string("$namespace.${property.name}") },
    )
}

public class LongPropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: Long, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): Long = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.long("$namespace.${property.name}") },
    )
}

public class BooleanPropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: Boolean, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): Boolean = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.boolean("$namespace.${property.name}") },
    )
}

public class DoublePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: Double, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): Double = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.double("$namespace.${property.name}") },
    )
}

public class LocalDatePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: LocalDate, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): LocalDate = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.localDate("$namespace.${property.name}") },
    )
}

public class LocalDateTimePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: LocalDateTime, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): LocalDateTime = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.localDateTime("$namespace.${property.name}") },
    )
}

public class OffsetDateTimePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: OffsetDateTime, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): OffsetDateTime = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.offsetDateTime("$namespace.${property.name}") },
    )
}

public class ListPropertyDelegate<out T> private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: List<T>, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): List<T> = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.list("$namespace.${property.name}") },
    )
}

public class MapPropertyDelegate<out T> private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val registry: ConfigRegistry,
) {

    public constructor(namespace: String = EMPTY, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.NoDefault, registry)

    public constructor(namespace: String = EMPTY, default: Map<String, T>, registry: ConfigRegistry = DefaultConfigRegistry): this(namespace, PropertyDefault.Default(default), registry)

    public operator fun getValue(thisRef: Any, property: KProperty<*>): Map<String, T> = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { registry.map("$namespace.${property.name}") },
    )
}

private inline fun <reified T> valueFromRegistry(
    thisRef: Any,
    property: KProperty<*>,
    namespace: String,
    default: PropertyDefault,
    crossinline retrieval: () -> T?,
): T {
    val namespaceWithoutProperty = when (namespace) {
        EMPTY -> thisRef::class.qualifiedName.toString()
        else -> namespace
    }
    val key = "$namespaceWithoutProperty.${property.name}"

    check("""^[a-zA-Z\d.]+$""".toRegex().matches(key)) { "Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$key]." }
    val value = retrieval.invoke()

    return when (default) {
        is PropertyDefault.Default<*> -> value ?: default.value as T
        is PropertyDefault.NoDefault -> value ?: throw IllegalStateException("Unable to find property [$key]. Property doesn't have a default set.")
    }
}