package com.arch.data.remote.model

import com.arch.domain.exception.ApiException

/**
 * Sealed class to represent API call result
 * Contains success body, error handling, and optional progress tracking
 */
sealed class ApiResponse<T> {
    /**
     * Successful API response with data
     */
    data class Success<T>(
        val data: T
    ) : ApiResponse<T>()

    /**
     * API returned error (non-2xx status)
     */
    data class Error<T>(
        val statusCode: Int,
        val errorBody: String? = null,
        val exception: ApiException
    ) : ApiResponse<T>()

    /**
     * Network IO error or connectivity issue
     */
    data class Exception<T>(
        val exception: ApiException
    ) : ApiResponse<T>()

    /**
     * Progress update for long-running operations
     */
    data class Loading<T>(
        val showProgress: Boolean = true
    ) : ApiResponse<T>()
}

/**
 * Extension function to check if response is success
 */
fun <T> ApiResponse<T>.isSuccess(): Boolean = this is ApiResponse.Success

/**
 * Extension function to get data or null
 */
fun <T> ApiResponse<T>.getDataOrNull(): T? = (this as? ApiResponse.Success)?.data

/**
 * Extension function to get exception or null
 */
fun <T> ApiResponse<T>.getExceptionOrNull(): ApiException? = when (this) {
    is ApiResponse.Error -> this.exception
    is ApiResponse.Exception -> this.exception
    else -> null
}

