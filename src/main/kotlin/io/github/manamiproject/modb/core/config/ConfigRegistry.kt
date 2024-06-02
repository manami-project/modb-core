package io.github.manamiproject.modb.core.config

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime

public interface ConfigRegistry {
    public fun string(key: String): String?
    public fun long(key: String): Long?
    public fun boolean(key: String): Boolean?
    public fun double(key: String): Double?
    public fun localDate(key: String): LocalDate?
    public fun localDateTime(key: String): LocalDateTime?
    public fun offsetDateTime(key: String): OffsetDateTime?
    public fun <T: Any> list(key: String): List<T>?
    public fun <T: Any> map(key: String): Map<String, T>?
}