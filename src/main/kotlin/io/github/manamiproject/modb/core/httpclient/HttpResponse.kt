package io.github.manamiproject.modb.core.httpclient

/**
 * @since 1.0.0
 */
public typealias HttpResponseCode = Int

/**
 * @since 1.0.0
 */
public typealias ResponseBody = String

/**
 * Data representing a HTTP response.
 * @since 1.0.0
 * @property code Numerical HTTP response code
 * @property body Raw response body
 * @property _headers All HTTP header sent by the server
 */
public data class HttpResponse(
        val code: HttpResponseCode,
        val body: ResponseBody,
        private val _headers: MutableMap<String, List<String>> = mutableMapOf()
) {
    /**
     * All HTTP header sent by the server in lower case.
     * @since 1.0.0
     */
    val headers: Map<String, List<String>>
        get() = _headers

    init {
        lowerCaseHeaders()
    }

    /**
     * Convenience function to indicate the status based on [code]
     * @since 1.0.0
     * @return `true` if the response code is 200
     */
    public fun isOk(): Boolean = code == 200

    private fun lowerCaseHeaders() {
        val lowerCaseKeyMap = _headers.map {
            it.key.toLowerCase() to it.value
        }

        _headers.clear()
        _headers.putAll(lowerCaseKeyMap)
    }
}