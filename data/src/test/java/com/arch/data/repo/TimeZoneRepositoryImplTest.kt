package com.arch.data.repo

import app.cash.turbine.test
import com.arch.data.base.BaseUnitTest
import com.arch.data.mapper.TimeZoneMapper.toDomain
import com.arch.data.remote.retrofit.api.TimeZoneApiService
import com.arch.data.remote.retrofit.api.TimeZoneResponse
import com.arch.domain.exception.ApiException
import com.arch.domain.exception.HttpException
import com.arch.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class TimeZoneRepositoryImplTest : BaseUnitTest() {
    private lateinit var repository: TimeZoneRepositoryImpl
    private lateinit var apiService: TimeZoneApiService

    private val mockTimeZoneResponse = TimeZoneResponse(
        abbreviation = "IST",
        clientIp = "192.168.1.1",
        datetime = "2026-02-18T16:30:00.000000+05:30",
        dayOfWeek = 2,
        dayOfYear = 49,
        dst = false,
        dstFrom = null,
        dstOffset = 0,
        dstUntil = null,
        rawOffset = 19800,
        timezone = "Asia/Kolkata",
        unixtime = 1771408200L,
        utcDatetime = "2026-02-18T11:00:00.000000+00:00",
        utcOffset = "+05:30",
        weekNumber = 8
    )

    @Before
    override fun setUp() {
        super.setUp()
        apiService = mockk()
        repository = TimeZoneRepositoryImpl(apiService)
    }

    @Test
    fun `getTimeZone emits loading then success when API call succeeds`() = runTest {
        // Given
        val timezone = "Asia/Kolkata"
        coEvery { apiService.getTimeZone(timezone) } returns Response.success(mockTimeZoneResponse)

        // When
        repository.getTimeZone(timezone, showProgress = true).test {
            // Then
            val loadingItem = awaitItem()
            assertTrue(loadingItem is Result.Loading)
            assertEquals(true, (loadingItem as Result.Loading).showProgress)

            val loadingCompleteItem = awaitItem()
            assertTrue(loadingCompleteItem is Result.Loading)
            assertEquals(false, (loadingCompleteItem as Result.Loading).showProgress)

            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertEquals(mockTimeZoneResponse.toDomain(), (successItem as Result.Success).data)

            awaitComplete()
        }

        coVerify { apiService.getTimeZone(timezone) }
    }

    @Test
    fun `getTimeZone without progress does not emit loading states`() = runTest {
        // Given
        val timezone = "UTC"
        coEvery { apiService.getTimeZone(timezone) } returns Response.success(mockTimeZoneResponse)

        // When
        repository.getTimeZone(timezone, showProgress = false).test {
            // Then
            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertEquals(mockTimeZoneResponse.toDomain(), (successItem as Result.Success).data)

            awaitComplete()
        }
    }

    @Test
    fun `getTimeZone emits error when API returns error response`() = runTest {
        // Given
        val timezone = "Invalid/Timezone"
        val errorBody = "Not found".toResponseBody(null)
        coEvery { apiService.getTimeZone(timezone) } returns Response.error(404, errorBody)

        // When
        repository.getTimeZone(timezone, showProgress = true).test {
            // Then
            awaitItem() // Loading true
            awaitItem() // Loading false

            val errorItem = awaitItem()
            assertTrue(errorItem is Result.Error)
            val exception = (errorItem as Result.Error).exception
            assertTrue(exception is HttpException)
            assertEquals(404, (exception as HttpException).code)

            awaitComplete()
        }
    }

    @Test
    fun `getTimeZone emits error when API throws exception`() = runTest {
        // Given
        val timezone = "Asia/Kolkata"
        val exception = ApiException("Network error")
        coEvery { apiService.getTimeZone(timezone) } throws exception

        // When
        repository.getTimeZone(timezone, showProgress = true).test {
            // Then
            awaitItem() // Loading true
            awaitItem() // Loading false

            val errorItem = awaitItem()
            assertTrue(errorItem is Result.Error)

            awaitComplete()
        }
    }

    @Test
    fun `getUtcTime calls getTimeZone with UTC parameter`() = runTest {
        // Given
        coEvery { apiService.getTimeZone("UTC") } returns Response.success(
            mockTimeZoneResponse.copy(timezone = "UTC")
        )

        // When
        repository.getUtcTime(showProgress = true).test {
            // Then
            awaitItem() // Loading true
            awaitItem() // Loading false

            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertEquals("UTC", (successItem as Result.Success).data.timezone)

            awaitComplete()
        }

        coVerify { apiService.getTimeZone("UTC") }
    }

    @Test
    fun `getUtcTime without progress does not emit loading states`() = runTest {
        // Given
        coEvery { apiService.getTimeZone("UTC") } returns Response.success(
            mockTimeZoneResponse.copy(timezone = "UTC")
        )

        // When
        repository.getUtcTime(showProgress = false).test {
            // Then
            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)

            awaitComplete()
        }
    }
}

