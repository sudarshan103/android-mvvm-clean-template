package com.arch.domain.model

import com.arch.domain.exception.ApiException

/**
 * A generic class that holds a value or an error status.
 * Used as a return type for use cases following clean architecture principles.
 */
sealed class Result<out T> {
    /**
     * Represents a successful result containing data.
     */
    data class Success<out T>(
        val data: T
    ) : Result<T>()

    /**
     * Represents a failed result containing an exception.
     */
    data class Error(
        val exception: ApiException
    ) : Result<Nothing>()

    /**
     * Represents a loading state with optional progress indicator flag.
     */
    data class Loading(
        val showProgress: Boolean = true
    ) : Result<Nothing>()

    /**
     * Returns true if this is a Success result.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns true if this is an Error result.
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns true if this is a Loading result.
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the data if this is a Success, or null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * Returns the exception if this is an Error, or null otherwise.
     */
    fun exceptionOrNull(): ApiException? = (this as? Error)?.exception

    /**
     * Transforms the data if this is a Success result.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            is Loading -> this
        }

    /**
     * Executes the given block if this is a Success result.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given block if this is an Error result.
     */
    inline fun onError(action: (ApiException) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }

    /**
     * Executes the given block if this is a Loading result.
     */
    inline fun onLoading(action: (Boolean) -> Unit): Result<T> {
        if (this is Loading) action(showProgress)
        return this
    }
}
