package io.github.manamiproject.modb.core.httpclient.retry

/**
 * Maximum number of retries performed, but the request still failed.
 * @since 1.0.0
 * @param message Individual message
 */
public class FailedAfterRetryException(message: String) : RuntimeException(message)