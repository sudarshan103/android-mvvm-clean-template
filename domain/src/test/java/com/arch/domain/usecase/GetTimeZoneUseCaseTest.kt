package com.arch.domain.usecase

import app.cash.turbine.test
import com.arch.domain.base.BaseUnitTest
import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import com.arch.domain.repository.TimeZoneRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTimeZoneUseCaseTest : BaseUnitTest() {
    private lateinit var useCase: GetTimeZoneUseCase
    private lateinit var repository: TimeZoneRepository

    private val mockTimeZone = TimeZone(
        abbreviation = "IST",
        datetime = "2026-02-18T16:30:00.000000+05:30",
        timezone = "Asia/Kolkata",
        utcOffset = "+05:30",
        dayOfWeek = 2,
        dayOfYear = 49,
        weekNumber = 8,
        unixTime = 1771408200L,
        isDst = false,
        dstOffset = 0,
        rawOffset = 19800,
        clientIp = null,
        dstFrom = null,
        dstUntil = null,
        utcDatetime = null
    )

    @Before
    override fun setUp() {
        super.setUp()
        repository = mockk()
        useCase = GetTimeZoneUseCase(repository)
    }

    @Test
    fun `invoke calls repository with correct parameters`() = runTest {
        // Given
        val timezone = "Asia/Kolkata"
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery {
            repository.getTimeZone(timezone, true)
        } returns flowOf(Result.Success(mockTimeZone))

        // When
        useCase(params).test {
            // Then
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(mockTimeZone, (item as Result.Success).data)
            awaitComplete()
        }

        coVerify { repository.getTimeZone(timezone, true) }
    }

    @Test
    fun `invoke with UTC timezone returns UTC data`() = runTest {
        // Given
        val timezone = "UTC"
        val utcTimeZone = mockTimeZone.copy(timezone = timezone)
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery {
            repository.getTimeZone(timezone, true)
        } returns flowOf(Result.Success(utcTimeZone))

        // When
        useCase(params).test {
            // Then
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(timezone, (item as Result.Success).data.timezone)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with showProgress false does not show progress`() = runTest {
        // Given
        val timezone = "Asia/Tokyo"
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = false)
        coEvery {
            repository.getTimeZone(timezone, false)
        } returns flowOf(Result.Success(mockTimeZone))

        // When
        useCase(params).test {
            // Then
            val item = awaitItem()
            assertTrue(item is Result.Success)
            awaitComplete()
        }

        coVerify { repository.getTimeZone(timezone, false) }
    }

    @Test
    fun `invoke emits loading states when repository emits loading`() = runTest {
        // Given
        val timezone = "Europe/London"
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery {
            repository.getTimeZone(timezone, true)
        } returns flowOf(
            Result.Loading(showProgress = true),
            Result.Success(mockTimeZone)
        )

        // When
        useCase(params).test {
            // Then
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)
            assertEquals(true, (loadingItem as Result.Loading).showProgress)

            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)

            awaitComplete()
        }
    }

    @Test
    fun `invoke emits error when repository fails`() = runTest {
        // Given
        val timezone = "Invalid/Timezone"
        val exception = com.arch.domain.exception.ApiException("Timezone not found")
        val params = GetTimeZoneUseCase.Params(timezone = timezone, showProgress = true)
        coEvery {
            repository.getTimeZone(timezone, true)
        } returns flowOf(Result.Error(exception))

        // When
        useCase(params).test {
            // Then
            val item = awaitItem()
            assertTrue(item is Result.Error)
            assertEquals(exception, (item as Result.Error).exception)
            awaitComplete()
        }
    }

    @Test
    fun `Params creates correct object with default showProgress`() {
        // When
        val params = GetTimeZoneUseCase.Params(timezone = "UTC")

        // Then
        assertEquals("UTC", params.timezone)
        assertTrue(params.showProgress)
    }

    @Test
    fun `Params creates correct object with custom showProgress`() {
        // When
        val params = GetTimeZoneUseCase.Params(timezone = "UTC", showProgress = false)

        // Then
        assertEquals("UTC", params.timezone)
        assertEquals(false, params.showProgress)
    }

    @Test
    fun `UTC constant has correct value`() {
        // Then
        assertEquals("UTC", GetTimeZoneUseCase.UTC)
    }
}
