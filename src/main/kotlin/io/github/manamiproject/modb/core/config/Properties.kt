package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.core.extensions.EMPTY
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.reflect.KProperty

/**
 * Configuration parameter returning a [String].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by StringPropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp="myValue"
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by StringPropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp="myValue"
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class StringPropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: String,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): String = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.string("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [Long].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by LongPropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=1535
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by LongPropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=1535
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class LongPropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: Long,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): Long = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.long("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [Boolean].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by BooleanPropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=true
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by BooleanPropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=true
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class BooleanPropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: Boolean,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): Boolean = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.boolean("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [Double].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by DoublePropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=10.4
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by DoublePropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=10.4
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class DoublePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: Double,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): Double = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.double("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [LocalDate].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by LocalDatePropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=2024-01-01
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by LocalDatePropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=2024-01-01
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class LocalDatePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: LocalDate,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): LocalDate = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.localDate("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [LocalDateTime].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by LocalDateTimePropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=2024-04-04T09:32:00
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by LocalDateTimePropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=2024-04-04T09:32:00
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class LocalDateTimePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: LocalDateTime,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): LocalDateTime = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.localDateTime("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [OffsetDateTime].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by OffsetDateTimePropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=2024-04-04T09:32:00+04:00
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by OffsetDateTimePropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=2024-04-04T09:32:00+04:00
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class OffsetDateTimePropertyDelegate private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: OffsetDateTime,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): OffsetDateTime = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.offsetDateTime("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [List].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by ListPropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=[
 *  "one",
 *  "two",
 * ]
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by ListPropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=[
 *  "one",
 *  "two",
 * ]
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class ListPropertyDelegate<out T: Any> private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: List<T>,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): List<T> = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.list("$namespace.${property.name}") },
    )
}

/**
 * Configuration parameter returning a [Set].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by SetPropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * org.example.project.Test.myProp=[
 *  "one",
 *  "two",
 * ]
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by SetPropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * something.different.myProp=[
 *  "one",
 *  "two",
 * ]
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class SetPropertyDelegate<out T: Any> private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: Set<T>,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): Set<T> = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.list<T>("$namespace.${property.name}")?.toSet() },
    )
}

/**
 * Configuration parameter returning a [Map].
 *
 * # Usage
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by MapPropertyDelegate()
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * [[org.example.project.Test]]
 * myProp={ key1="value1", key2="value2" }
 * ```
 *
 * # Custom namespace
 * Let's assume:
 *
 * ```kotlin
 * package org.example.project
 *
 * class Test {
 *   private val myProp by MapPropertyDelegate(namespace = "something.different")
 * }
 * ```
 *
 * Then you property would look like this:
 *
 * ```toml
 * [[org.example.project.Test]]
 * myProp={ key1="value1", key2="value2" }
 * ```
 *
 * # Testing
 *
 * You can pass a mocked [ConfigRegistry] for testing.
 * @since 13.0.0
 * @property namespace The prefix of a fully qualified property name. It will internally be exted by the variable name.
 * @property default Default value in case the property cannot be found.
 * @property configRegistry Handles the retrieval of the value.
 */
public class MapPropertyDelegate<out T: Any> private constructor(
    private val namespace: String,
    private val default: PropertyDefault,
    private val configRegistry: ConfigRegistry,
) {

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.NoDefault, configRegistry)

    /**
     * @since 13.0.0
     * @param namespace The prefix of a fully qualified property name. It will internally be exted by the variable name. **Default:** [EMPTY]
     * @param default Default value in case the property cannot be found.
     * @param configRegistry Handles the retrieval of the value. **Default:** [DefaultConfigRegistry.instance]
     */
    public constructor(
        namespace: String = EMPTY,
        default: Map<String, T>,
        configRegistry: ConfigRegistry = DefaultConfigRegistry.instance,
    ): this(namespace, PropertyDefault.Default(default), configRegistry)

    /**
     * This allows you to use this class as property delegate using `by` keyword.
     * @since 13.0.0
     * @param thisRef Calling class
     * @param property Property to which the value will be assigned to. It is also part of the fully qualified property name.
     */
    public operator fun getValue(thisRef: Any, property: KProperty<*>): Map<String, T> = valueFromRegistry(
        thisRef = thisRef,
        property = property,
        namespace = namespace,
        default = default,
        retrieval = { configRegistry.map("$namespace.${property.name}") },
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