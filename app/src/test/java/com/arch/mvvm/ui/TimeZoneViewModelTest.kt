package com.arch.mvvm.ui

import app.cash.turbine.test
import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import com.arch.domain.usecase.GetTimeZoneUseCase
import com.arch.mvvm.base.BaseViewModelTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimeZoneViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: TimeZoneViewModel
    private lateinit var getTimeZoneUseCase: GetTimeZoneUseCase

    private val mockTimeZone = TimeZone(
        abbreviation = "UTC",
        datetime = "2026-02-18T10:30:00.000000+00:00",
        timezone = "UTC",
        utcOffset = "+00:00",
        dayOfWeek = 2,
        dayOfYear = 49,
        weekNumber = 8,
        unixTime = 1771408200L,
        isDst = false,
        dstOffset = 0,
        rawOffset = 0,
        clientIp = null,
        dstFrom = null,
        dstUntil = null,
        utcDatetime = null
    )

    @Before
    override fun setUp() {
        super.setUp()
        getTimeZoneUseCase = mockk()
        viewModel = TimeZoneViewModel(getTimeZoneUseCase)
    }

    @Test
    fun `fetchTimeZone with UTC emits loading then success to utcTimeState`() = runTest {
        // Given
        val params = GetTimeZoneUseCase.Params(timezone = "UTC", showProgress = true)
        coEvery { getTimeZoneUseCase(params) } returns flowOf(
            Result.Loading(showProgress = true),
            Result.Success(mockTimeZone)
        )

        // When
        viewModel.fetchTimeZone("UTC")
        advanceUntilIdle()

        // Then
        viewModel.utcTimeState.test {
            val item = awaitItem()
            assert(item is Result.Success)
            assertEquals(mockTimeZone, (item as Result.Success).data)
        }
        coVerify { getTimeZoneUseCase(params) }
    }

    @Test
    fun `fetchTimeZone with non-UTC timezone emits to timeZoneState`() = runTest {
        // Given
        val timezone = "Asia/Kolkata"
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery { getTimeZoneUseCase(params) } returns flowOf(
            Result.Loading(showProgress = true),
            Result.Success(mockTimeZone.copy(timezone = timezone))
        )

        // When
        viewModel.fetchTimeZone(timezone)
        advanceUntilIdle()

        // Then
        viewModel.timeZoneState.test {
            val item = awaitItem()
            assert(item is Result.Success)
            assertEquals(timezone, (item as Result.Success).data.timezone)
        }
        coVerify { getTimeZoneUseCase(params) }
    }

    @Test
    fun `fetchTimeZone on error updates errorMessage`() = runTest {
        // Given
        val timezone = "UTC"
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery { getTimeZoneUseCase(params) } returns flowOf(
            Result.Error(com.arch.domain.exception.ApiException(errorMessage, exception))
        )

        // When
        viewModel.fetchTimeZone(timezone)
        advanceUntilIdle()

        // Then
        viewModel.errorMessage.test {
            assertEquals(errorMessage, awaitItem())
        }
    }

    @Test
    fun `fetchTimeZone on success clears errorMessage`() = runTest {
        // Given
        val timezone = "UTC"
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery { getTimeZoneUseCase(params) } returns flowOf(
            Result.Success(mockTimeZone)
        )

        // When
        viewModel.fetchTimeZone(timezone)
        advanceUntilIdle()

        // Then
        viewModel.errorMessage.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `clearError clears error message`() = runTest {
        // Given
        val timezone = "UTC"
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery { getTimeZoneUseCase(params) } returns flowOf(
            Result.Error(com.arch.domain.exception.ApiException(errorMessage, exception))
        )
        viewModel.fetchTimeZone(timezone)
        advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        viewModel.errorMessage.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `fetchTimeZone on loading does not update error message`() = runTest {
        // Given
        val timezone = "UTC"
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery { getTimeZoneUseCase(params) } returns flowOf(
            Result.Loading(showProgress = true)
        )

        // When
        viewModel.fetchTimeZone(timezone)
        advanceUntilIdle()

        // Then
        viewModel.errorMessage.test {
            assertNull(awaitItem())
        }
    }
}

