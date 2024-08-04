package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.core.TestConfigRegistry
import io.github.manamiproject.modb.core.TestKProperty
import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.reflect.KProperty
import kotlin.test.Test

internal class PropertiesKtTest {

    @Nested
    inner class StringPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String? = null
            }

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            val property = StringPropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `returns null if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String? = null
            }

            val property = StringPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String? = null
            }

            val property = StringPropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String? = null
            }

            val property = StringPropertyDelegate(
                namespace = "modb.core.unittest",
                default = "my-default",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo("my-default")
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "my-value"
            }

            val property = StringPropertyDelegate(
                namespace = "modb.core.unittest",
                default = "my-default",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo("my-value")
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "my-value"
            }

            val property = StringPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo("my-value")
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "my-value"
            }

            val property = StringPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.endsWith("value") },
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo("my-value")
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun string(key: String): String = "my-value"
            }

            val property = StringPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.endsWith("non-existent") },
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [my-value] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class LongPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long? = null
            }

            val property = LongPropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long? = null
            }

            val property = LongPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long? = null
            }

            val property = LongPropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long? = null
            }

            val property = LongPropertyDelegate(
                namespace = "modb.core.unittest",
                default = 456L,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(456L)
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long = 128L
            }

            val property = LongPropertyDelegate(
                namespace = "modb.core.unittest",
                default = 456L,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128L)
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long = 128L
            }

            val property = LongPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128L)
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long = 128L
            }

            val property = LongPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value > 100L }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128L)
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun long(key: String): Long = 128L
            }

            val property = LongPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value < 100L }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [128] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class IntPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int? = null
            }

            val property = IntPropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int? = null
            }

            val property = IntPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int? = null
            }

            val property = IntPropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int? = null
            }

            val property = IntPropertyDelegate(
                namespace = "modb.core.unittest",
                default = 456,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(456)
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int = 128
            }

            val property = IntPropertyDelegate(
                namespace = "modb.core.unittest",
                default = 456,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128)
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int = 128
            }

            val property = IntPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128)
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int = 128
            }

            val property = IntPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value > 100 }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128)
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun int(key: String): Int = 128
            }

            val property = IntPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value < 100 }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [128] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class BooleanPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean? = null
            }

            val property = BooleanPropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean? = null
            }

            val property = BooleanPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean? = null
            }

            val property = BooleanPropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean? = null
            }

            val property = BooleanPropertyDelegate(
                namespace = "modb.core.unittest",
                default = false,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean = true
            }

            val property = BooleanPropertyDelegate(
                namespace = "modb.core.unittest",
                default = false,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean = true
            }

            val property = BooleanPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean = true
            }

            val property = BooleanPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun boolean(key: String): Boolean = true
            }

            val property = BooleanPropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> !value }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [true] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class DoublePropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double? = null
            }

            val property = DoublePropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double? = null
            }

            val property = DoublePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double? = null
            }

            val property = DoublePropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double? = null
            }

            val property = DoublePropertyDelegate(
                namespace = "modb.core.unittest",
                default = 456.12,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(456.12)
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double = 128.43
            }

            val property = DoublePropertyDelegate(
                namespace = "modb.core.unittest",
                default = 456.12,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128.43)
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double = 128.43
            }

            val property = DoublePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128.43)
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double = 128.43
            }

            val property = DoublePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value > 100.0 }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(128.43)
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun double(key: String): Double = 128.43
            }

            val property = DoublePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value < 100.0 }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [128.43] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class LocalDatePropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate? = null
            }

            val property = LocalDatePropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate? = null
            }

            val property = LocalDatePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate? = null
            }

            val property = LocalDatePropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val defaultValue = LocalDate.now()

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate? = null
            }

            val property = LocalDatePropertyDelegate(
                namespace = "modb.core.unittest",
                default = defaultValue,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(defaultValue)
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val defaultValue = LocalDate.now()
            val value = defaultValue.minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate = value
            }

            val property = LocalDatePropertyDelegate(
                namespace = "modb.core.unittest",
                default = defaultValue,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val value = LocalDate.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate = value
            }

            val property = LocalDatePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val date = LocalDate.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate = date
            }

            val property = LocalDatePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isBefore(LocalDate.now()) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(date)
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val date = LocalDate.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDate(key: String): LocalDate = date
            }

            val property = LocalDatePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isAfter(LocalDate.now()) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [${date}] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class LocalDateTimePropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime? = null
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime? = null
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime? = null
            }

            val property = LocalDateTimePropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val defaultValue = LocalDateTime.now()

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime? = null
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                default = defaultValue,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(defaultValue)
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val defaultValue = LocalDateTime.now()
            val value = defaultValue.minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime = value
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                default = defaultValue,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val value = LocalDateTime.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime = value
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val date = LocalDateTime.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime = date
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isBefore(LocalDateTime.now()) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(date)
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val date = LocalDateTime.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun localDateTime(key: String): LocalDateTime = date
            }

            val property = LocalDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isAfter(LocalDateTime.now()) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [$date] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class OffsetDateTimePropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime? = null
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime? = null
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime? = null
            }

            val property = OffsetDateTimePropertyDelegate(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val defaultValue = OffsetDateTime.now()

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime? = null
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                default = defaultValue,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(defaultValue)
        }

        @Test
        fun `returns value when a default has been defined`() {
            // given
            val defaultValue = OffsetDateTime.now()
            val value = defaultValue.minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime = value
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                default = defaultValue,
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `returns value when no default has been defined`() {
            // given
            val value = OffsetDateTime.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime = value
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `correctly returns value when custom validator matches`() {
            // given
            val date = OffsetDateTime.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime = date
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isBefore(OffsetDateTime.now()) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(date)
        }

        @Test
        fun `throws exception if validator returns false`() {
            // given
            val date = OffsetDateTime.now().minusDays(2)

            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun offsetDateTime(key: String): OffsetDateTime = date
            }

            val property = OffsetDateTimePropertyDelegate(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isAfter(OffsetDateTime.now()) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [$date] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class ListPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            val property = ListPropertyDelegate<String>(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val property = ListPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val property = ListPropertyDelegate<String>(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val property = ListPropertyDelegate(
                namespace = "modb.core.unittest",
                default = listOf("my-default"),
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(listOf("my-default"))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf("my-value") as List<T>
            }

            val property = ListPropertyDelegate(
                namespace = "modb.core.unittest",
                default = listOf("my-default"),
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(listOf("my-value"))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf("my-value") as List<T>
            }

            val property = ListPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(listOf("my-value"))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf("my-value") as List<T>
            }

            val property = ListPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isNotEmpty() }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(listOf("my-value"))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf("my-value") as List<T>
            }

            val property = ListPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isEmpty() }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [my-value] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class SetPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            val property = SetPropertyDelegate<String>(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val property = SetPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val property = SetPropertyDelegate<String>(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T>? = null
            }

            val property = SetPropertyDelegate(
                namespace = "modb.core.unittest",
                default = setOf("my-default"),
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(setOf("my-default"))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf(
                    "one",
                    "one",
                    "two",
                    "two",
                ) as List<T>
            }

            val property = SetPropertyDelegate(
                namespace = "modb.core.unittest",
                default = setOf("my-default"),
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(setOf(
                "one",
                "two",
            ))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf(
                    "one",
                    "one",
                    "two",
                    "two",
                ) as List<T>
            }

            val property = SetPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(setOf(
                "one",
                "two",
            ))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf(
                    "one",
                    "one",
                    "two",
                    "two",
                ) as List<T>
            }

            val property = SetPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isNotEmpty() }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(setOf(
                "one",
                "two",
            ))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> list(key: String): List<T> = listOf(
                    "one",
                    "one",
                    "two",
                    "two",
                ) as List<T>
            }

            val property = SetPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.isEmpty() }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [one, two] for property [modb.core.unittest.testProp] is invalid.")
        }
    }

    @Nested
    inner class MapPropertyDelegateTests {

        @ParameterizedTest
        @ValueSource(strings = [" ", "!abv", "abc-def"])
        fun `throws an exception if the namespace is not valid`(input: String) {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T>? = null
            }

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            val property = MapPropertyDelegate<Long>(
                namespace = input,
                configRegistry = testConfigRegistry,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Config parameter can only consist of alphanumeric chars and dots. Adjust the namespace of [$input.testProp].")
        }

        @Test
        fun `throws an exception if the value is not present and there is not default`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T>? = null
            }

            val property = MapPropertyDelegate<Long>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [modb.core.unittest.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `default namespace is qualified name of followed by property name`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T>? = null
            }

            val property = MapPropertyDelegate<Long>(
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Unable to find property [kotlinx.coroutines.StandaloneCoroutine.testProp]. Property doesn't have a default set.")
        }

        @Test
        fun `returns default value if value is not set and a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T>? = null
            }

            val property = MapPropertyDelegate(
                namespace = "modb.core.unittest",
                default = mapOf("my-default" to 5432L),
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(mapOf(
                "my-default" to 5432L,
            ))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `returns value when a default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T> = mapOf("my-value" to 8080L) as Map<String, T>
            }

            val property = MapPropertyDelegate(
                namespace = "modb.core.unittest",
                default = mapOf("my-default" to 5432L),
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(mapOf("my-value" to 8080L))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `returns value when no default has been defined`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T> = mapOf("my-value" to 8080L) as Map<String, T>
            }

            val property = MapPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(mapOf("my-value" to 8080L))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `correctly returns value when custom validator matches`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T> = mapOf("my-value" to 8080L) as Map<String, T>
            }

            val property = MapPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.containsValue<String, Any>(8080L) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = property.getValue(this, testKProperty)

            // then
            assertThat(result).isEqualTo(mapOf("my-value" to 8080L))
        }

        @Test
        @Suppress("UNCHECKED_CAST")
        fun `throws exception if validator returns false`() {
            // given
            val testConfigRegistry = object: ConfigRegistry by TestConfigRegistry {
                override fun <T: Any> map(key: String): Map<String, T> = mapOf("my-value" to 8080L) as Map<String, T>
            }

            val property = MapPropertyDelegate<String>(
                namespace = "modb.core.unittest",
                configRegistry = testConfigRegistry,
                validator = { value -> value.containsValue<String, Any>(443L) }
            )

            val testKProperty = object: KProperty<String> by TestKProperty() {
                override val name: String = "testProp"
            }

            // when
            val result = exceptionExpected<IllegalStateException> {
                property.getValue(this, testKProperty)
            }

            // then
            assertThat(result).hasMessage("Value [{my-value=8080}] for property [modb.core.unittest.testProp] is invalid.")
        }
    }
}