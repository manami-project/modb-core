package io.github.manamiproject.modb.core.httpclient

/**
 * @since 1.0.0
 */
const val APPLICATION_JSON = "application/json"

/**
 * Data representing a HTTP request body
 * @since 1.0.0
 * @property mediaType Actual media type of the request body which will be used in the content-type header.
 * @property body HTTP request body as [String]
 */
data class RequestBody(
    val mediaType: String,
    val body: String
)