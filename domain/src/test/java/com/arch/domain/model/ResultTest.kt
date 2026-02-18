package com.arch.domain.model

import com.arch.domain.base.BaseUnitTest
import com.arch.domain.exception.ApiException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest : BaseUnitTest() {
    @Test
    fun `Success creates result with data`() {
        // When
        val result = Result.Success("test data")

        // Then
        assertEquals("test data", result.data)
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
    }

    @Test
    fun `Error creates result with exception`() {
        // Given
        val exception = ApiException("Error occurred")

        // When
        val result = Result.Error(exception)

        // Then
        assertEquals(exception, result.exception)
        assertFalse(result.isSuccess)
        assertTrue(result.isError)
        assertFalse(result.isLoading)
    }

    @Test
    fun `Loading creates result with showProgress flag`() {
        // When
        val result = Result.Loading(showProgress = true)

        // Then
        assertTrue(result.showProgress)
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
        assertTrue(result.isLoading)
    }

    @Test
    fun `getOrNull returns data for Success`() {
        // Given
        val result = Result.Success("test data")

        // When
        val data = result.getOrNull()

        // Then
        assertEquals("test data", data)
    }

    @Test
    fun `getOrNull returns null for Error`() {
        // Given
        val result = Result.Error(ApiException("Error"))

        // When
        val data = result.getOrNull()

        // Then
        assertNull(data)
    }

    @Test
    fun `getOrNull returns null for Loading`() {
        // Given
        val result = Result.Loading()

        // When
        val data = result.getOrNull()

        // Then
        assertNull(data)
    }

    @Test
    fun `exceptionOrNull returns exception for Error`() {
        // Given
        val exception = ApiException("Error occurred")
        val result = Result.Error(exception)

        // When
        val resultException = result.exceptionOrNull()

        // Then
        assertEquals(exception, resultException)
    }

    @Test
    fun `exceptionOrNull returns null for Success`() {
        // Given
        val result = Result.Success("data")

        // When
        val exception = result.exceptionOrNull()

        // Then
        assertNull(exception)
    }

    @Test
    fun `map transforms data for Success`() {
        // Given
        val result = Result.Success(5)

        // When
        val mappedResult = result.map { it * 2 }

        // Then
        assertTrue(mappedResult is Result.Success)
        assertEquals(10, (mappedResult as Result.Success).data)
    }

    @Test
    fun `map preserves Error`() {
        // Given
        val exception = ApiException("Error")
        val result: Result<Int> = Result.Error(exception)

        // When
        val mappedResult = result.map { it * 2 }

        // Then
        assertTrue(mappedResult is Result.Error)
        assertEquals(exception, (mappedResult as Result.Error).exception)
    }

    @Test
    fun `map preserves Loading`() {
        // Given
        val result: Result<Int> = Result.Loading(showProgress = true)

        // When
        val mappedResult = result.map { it * 2 }

        // Then
        assertTrue(mappedResult is Result.Loading)
        assertTrue((mappedResult as Result.Loading).showProgress)
    }

    @Test
    fun `onSuccess executes action for Success`() {
        // Given
        val result = Result.Success("test")
        var executed = false

        // When
        result.onSuccess { executed = true }

        // Then
        assertTrue(executed)
    }

    @Test
    fun `onSuccess does not execute action for Error`() {
        // Given
        val result: Result<String> = Result.Error(ApiException("Error"))
        var executed = false

        // When
        result.onSuccess { executed = true }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `onError executes action for Error`() {
        // Given
        val exception = ApiException("Error")
        val result = Result.Error(exception)
        var executedException: ApiException? = null

        // When
        result.onError { executedException = it }

        // Then
        assertEquals(exception, executedException)
    }

    @Test
    fun `onError does not execute action for Success`() {
        // Given
        val result = Result.Success("data")
        var executed = false

        // When
        result.onError { executed = true }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `onLoading executes action for Loading`() {
        // Given
        val result = Result.Loading(showProgress = true)
        var showProgress: Boolean? = null

        // When
        result.onLoading { showProgress = it }

        // Then
        assertEquals(true, showProgress)
    }

    @Test
    fun `onLoading does not execute action for Success`() {
        // Given
        val result = Result.Success("data")
        var executed = false

        // When
        result.onLoading { executed = true }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `chaining onSuccess onError onLoading works correctly`() {
        // Given
        val result = Result.Success("data")
        var successCalled = false
        var errorCalled = false
        var loadingCalled = false

        // When
        result
            .onSuccess { successCalled = true }
            .onError { errorCalled = true }
            .onLoading { loadingCalled = true }

        // Then
        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertFalse(loadingCalled)
    }
}

