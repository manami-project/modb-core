package io.github.manamiproject.modb.core.json

import com.squareup.moshi.JsonDataException
import io.github.manamiproject.modb.test.exceptionExpected
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TagAdapterTest {

    @Nested
    inner class FromJsonTests {

        @Test
        fun `correctly deserialize non-null value`() {
            // given
            val adapter = TagAdapter()

            // when
            val result = adapter.fromJson("\"mystery\"")

            // then
            assertThat(result).isEqualTo("mystery")
        }

        @Test
        fun `throw exception on null value`() {
            // given
            val adapter = TagAdapter()

            // when
            val result = exceptionExpected<JsonDataException> {
                adapter.fromJson("""null""")
            }

            // then
            assertThat(result).hasMessage("Expected a string but was NULL at path \$")
        }
    }

    @Nested
    inner class ToJsonTests {

        @Test
        fun `correctly serialize non-null value`() {
            // given
            val adapter = TagAdapter()
            val obj = "mystery"

            // when
            val result = adapter.toJson(obj)

            // then
            assertThat(result).isEqualTo("\"mystery\"")
        }

        @Test
        fun `throws exception for a null value`() {
            // given
            val adapter = TagAdapter().serializeNulls()

            // when
            val result = exceptionExpected<IllegalArgumentException> {
                adapter.toJson(null)
            }

            // then
            assertThat(result).hasMessage("TagAdapter is non-nullable, but received null.")
        }
    }
}