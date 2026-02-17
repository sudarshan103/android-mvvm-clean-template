package com.arch.domain.exception

/**
 * Exception thrown when internet is not enabled or network is unavailable
 */
class InternetNotEnabledException(
    message: String = "Internet is not enabled",
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Base exception for API errors
 */
open class ApiException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Exception thrown for non-200 HTTP responses
 */
class HttpException(
    val code: Int,
    message: String,
    val errorBody: String? = null,
    cause: Throwable? = null
) : ApiException(message, cause)

/**
 * Exception thrown for network IO errors
 */
class NetworkIOException(
    message: String = "Network IO error",
    cause: Throwable? = null
) : ApiException(message, cause)

