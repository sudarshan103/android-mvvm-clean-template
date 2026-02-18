package com.arch.data.remote.model

import com.arch.data.base.BaseUnitTest
import com.arch.domain.exception.ApiException
import com.arch.domain.exception.HttpException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiResponseTest : BaseUnitTest() {
    @Test
    fun `Success creates ApiResponse with data`() {
        // When
        val response = ApiResponse.Success("test data")

        // Then
        assertEquals("test data", response.data)
    }

    @Test
    fun `Error creates ApiResponse with exception and status code`() {
        // Given
        val exception = HttpException(code = 404, message = "Not found")

        // When
        val response = ApiResponse.Error<String>(
            statusCode = 404,
            errorBody = "Not found",
            exception = exception
        )

        // Then
        assertEquals(404, response.statusCode)
        assertEquals("Not found", response.errorBody)
        assertEquals(exception, response.exception)
    }

    @Test
    fun `Exception creates ApiResponse with exception`() {
        // Given
        val exception = ApiException("Network error")

        // When
        val response = ApiResponse.Exception<String>(exception)

        // Then
        assertEquals(exception, response.exception)
    }

    @Test
    fun `Loading creates ApiResponse with showProgress flag`() {
        // When
        val response = ApiResponse.Loading<String>(showProgress = true)

        // Then
        assertTrue(response.showProgress)
    }

    @Test
    fun `isSuccess returns true for Success response`() {
        // Given
        val response = ApiResponse.Success("data")

        // When
        val result = response.isSuccess()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isSuccess returns false for Error response`() {
        // Given
        val response = ApiResponse.Error<String>(
            statusCode = 500,
            exception = HttpException(500, "Error")
        )

        // When
        val result = response.isSuccess()

        // Then
        assertFalse(result)
    }

    @Test
    fun `getDataOrNull returns data for Success response`() {
        // Given
        val response = ApiResponse.Success("test data")

        // When
        val result = response.getDataOrNull()

        // Then
        assertEquals("test data", result)
    }

    @Test
    fun `getDataOrNull returns null for Error response`() {
        // Given
        val response = ApiResponse.Error<String>(
            statusCode = 404,
            exception = HttpException(404, "Not found")
        )

        // When
        val result = response.getDataOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `getExceptionOrNull returns exception for Error response`() {
        // Given
        val exception = HttpException(500, "Server error")
        val response = ApiResponse.Error<String>(
            statusCode = 500,
            exception = exception
        )

        // When
        val result = response.getExceptionOrNull()

        // Then
        assertEquals(exception, result)
    }

    @Test
    fun `getExceptionOrNull returns exception for Exception response`() {
        // Given
        val exception = ApiException("Network error")
        val response = ApiResponse.Exception<String>(exception)

        // When
        val result = response.getExceptionOrNull()

        // Then
        assertEquals(exception, result)
    }

    @Test
    fun `getExceptionOrNull returns null for Success response`() {
        // Given
        val response = ApiResponse.Success("data")

        // When
        val result = response.getExceptionOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `getExceptionOrNull returns null for Loading response`() {
        // Given
        val response = ApiResponse.Loading<String>()

        // When
        val result = response.getExceptionOrNull()

        // Then
        assertNull(result)
    }
}

