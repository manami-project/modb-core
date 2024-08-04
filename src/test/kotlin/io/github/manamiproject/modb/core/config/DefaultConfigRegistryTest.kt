package io.github.manamiproject.modb.core.config

import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.testResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import kotlin.test.Test

internal class DefaultConfigRegistryTest {

    @Nested
    inner class StringTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "string.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.string(key)

            // then
            assertThat(result).isEqualTo("exists-only-in-classpath")
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "string.override.envVar"
            val value = "expected-value"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value,
            ))

            // when
            val result = configRegistry.string(key)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "string.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.string(key)

            // then
            assertThat(result).isEqualTo("other-file-override")
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "string.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.string(key)

            // then
            assertThat(result).isEqualTo("8")
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "string.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.string(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class LongTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "long.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.long(key)

            // then
            assertThat(result).isEqualTo(5432L)
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "long.override.envVar"
            val value = 443L
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value.toString(),
            ))

            // when
            val result = configRegistry.long(key)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "long.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.long(key)

            // then
            assertThat(result).isEqualTo(21L)
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "long.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.long(key)

            // then
            assertThat(result).isEqualTo(8L)
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "long.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.long(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if type is wrong`() {
            // given
            val key = "long.null.on.wrong.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.long(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class IntTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "int.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.int(key)

            // then
            assertThat(result).isEqualTo(5432)
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "int.override.envVar"
            val value = 443
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value.toString(),
            ))

            // when
            val result = configRegistry.int(key)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "int.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.int(key)

            // then
            assertThat(result).isEqualTo(21)
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "int.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.int(key)

            // then
            assertThat(result).isEqualTo(8)
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "int.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.int(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if type is wrong`() {
            // given
            val key = "int.null.on.wrong.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.int(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class BooleanTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "boolean.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "long.override.envVar"
            val value = true
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value.toString(),
            ))

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "boolean.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "boolean.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `returns null if key doesn't exist`() {
            // given
            val key = "boolean.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `returns null if type is wrong`() {
            // given
            val key = "boolean.null.on.wrong.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `returns null if value is not strictly boolean format`() {
            // given
            val key = "boolean.null.on.boolean.strict"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.boolean(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class DoubleTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "double.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.double(key)

            // then
            assertThat(result).isEqualTo(128.42)
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "double.override.envVar"
            val value = 256.77
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value.toString(),
            ))

            // when
            val result = configRegistry.double(key)

            // then
            assertThat(result).isEqualTo(value)
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "double.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.double(key)

            // then
            assertThat(result).isEqualTo(512.33)
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "double.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.double(key)

            // then
            assertThat(result).isEqualTo(8.0)
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "double.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.double(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if type is wrong`() {
            // given
            val key = "double.null.on.wrong.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.double(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class LocalDateTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "localDate.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDate(key)

            // then
            assertThat(result).isEqualTo(LocalDate.of(2021, 1, 1))
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "localDate.override.envVar"
            val value = "2024-06-20"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value,
            ))

            // when
            val result = configRegistry.localDate(key)

            // then
            assertThat(result).isEqualTo(LocalDate.of(2024, 6, 20))
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "localDate.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.localDate(key)

            // then
            assertThat(result).isEqualTo(LocalDate.of(2024, 5, 15))
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "localDate.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDate(key)

            // then
            assertThat(result).isEqualTo(LocalDate.of(2024, 4, 4))
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "localDate.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDate(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if value cannot be parsed`() {
            // given
            val key = "localDate.null.on.wrong.format"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDate(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class LocalDateTimeTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "localDateTime.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDateTime(key)

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2021, 1, 1, 6, 32, 9))
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "localDateTime.override.envVar"
            val value = "2024-04-04T11:15:25"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value,
            ))

            // when
            val result = configRegistry.localDateTime(key)

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2024, 4, 4, 11, 15, 25))
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "localDateTime.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.localDateTime(key)

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2023, 3, 3, 10, 32, 0))
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "localDateTime.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDateTime(key)

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2024, 4, 4, 9, 32, 0))
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "localDateTime.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDateTime(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if value cannot be parsed`() {
            // given
            val key = "localDateTime.null.on.wrong.format"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.localDateTime(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class OffsetDateTimeTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "offsetDateTime.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.offsetDateTime(key)

            // then
            assertThat(result).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2021, 1, 1, 6, 32, 9), UTC))
        }

        @Test
        fun `override by environment variable`() {
            // given
            val key = "offsetDateTime.override.envVar"
            val value = "2024-04-04T11:15:25+06:00"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to value,
            ))

            // when
            val result = configRegistry.offsetDateTime(key)

            // then
            assertThat(result).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2024, 4, 4, 11, 15, 25), ZoneOffset.ofHours(6)))
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "offsetDateTime.override.file"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.offsetDateTime(key)

            // then
            assertThat(result).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2023, 3, 3, 10, 32, 0), ZoneOffset.ofHours(5)))
        }

        @Test
        fun `can cast different types`() {
            // given
            val key = "offsetDateTime.different.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.offsetDateTime(key)

            // then
            assertThat(result).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2024, 4, 4, 9, 32, 0), ZoneOffset.ofHours(4)))
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "offsetDateTime.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.offsetDateTime(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if value cannot be parsed`() {
            // given
            val key = "offsetDateTime.null.on.wrong.format"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.offsetDateTime(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class ListTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "list.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.list<String>(key)

            // then
            assertThat(result).containsExactlyInAnyOrder(
                "one",
                "two",
                "three",
            )
        }

        @Test
        fun `throws error when environment variable is set`() {
            // given
            val key = "list.override.envVar"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to "something",
            ))

            // when
            val result = exceptionExpected<IllegalStateException> {
                configRegistry.list<String>(key)
            }

            // then
            assertThat(result).hasMessage("Environment variable is not supported for property of type list. See [list.override.envVar]")
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "list.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.list<Long>(key)

            // then
            assertThat(result).containsExactlyInAnyOrder(
                456,
                789,
            )
        }

        @Test
        fun `wrap a single element in a list`() {
            // given
            val key = "list.single.element"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.list<String>(key)

            // then
            assertThat(result).containsExactlyInAnyOrder(
                "single-element",
            )
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "list.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.list<String>(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class MapTests {

        @Test
        fun `correctly find property from config file in classpath`() {
            // given
            val key = "map.classpath.exclusive"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.map<String>(key)

            // then
            assertThat(result).containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "key1" to "value1",
                    "key2" to "value2",
                )
            )
        }

        @Test
        fun `throws error when environment variable is set`() {
            // given
            val key = "map.override.envVar"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                key to "something",
            ))

            // when
            val result = exceptionExpected<IllegalStateException> {
                configRegistry.map<String>(key)
            }

            // then
            assertThat(result).hasMessage("Environment variable is not supported for property of type map. See [map.override.envVar]")
        }

        @Test
        fun `override by custom config file`() {
            // given
            val key = "map.override.file"
            val configRegistry = DefaultConfigRegistry(environmentVariables = mapOf(
                "modb.core.config.location" to testResource("default_config_registry_tests/override-config.toml").toAbsolutePath().toString(),
            ))

            // when
            val result = configRegistry.map<Long>(key)

            // then
            assertThat(result).containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "key4" to 876,
                    "key5" to 534,
                    "key6" to 312,
                )
            )
        }

        @Test
        fun `return null if key doesn't exist`() {
            // given
            val key = "map.key.not.exists"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.map<String>(key)

            // then
            assertThat(result).isNull()
        }

        @Test
        fun `return null if type is wrong`() {
            // given
            val key = "maptest.null.on.wrong.type"
            val configRegistry = DefaultConfigRegistry()

            // when
            val result = configRegistry.map<String>(key)

            // then
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class CompanionObjectTests {

        @Test
        fun `instance property always returns same instance`() {
            // given
            val previous = DefaultConfigRegistry.instance

            // when
            val result = DefaultConfigRegistry.instance

            // then
            assertThat(result).isExactlyInstanceOf(DefaultConfigRegistry::class.java)
            assertThat(result===previous).isTrue()
        }
    }
}