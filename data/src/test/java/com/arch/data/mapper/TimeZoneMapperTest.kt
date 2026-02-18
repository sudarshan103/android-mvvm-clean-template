package com.arch.data.mapper

import com.arch.data.base.BaseUnitTest
import com.arch.data.remote.retrofit.api.TimeZoneResponse
import com.arch.data.mapper.TimeZoneMapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeZoneMapperTest : BaseUnitTest() {
    @Test
    fun `toDomain maps all fields correctly`() {
        // Given
        val response = TimeZoneResponse(
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

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("IST", domain.abbreviation)
        assertEquals("192.168.1.1", domain.clientIp)
        assertEquals("2026-02-18T16:30:00.000000+05:30", domain.datetime)
        assertEquals(2, domain.dayOfWeek)
        assertEquals(49, domain.dayOfYear)
        assertEquals(false, domain.isDst)
        assertEquals(null, domain.dstFrom)
        assertEquals(0, domain.dstOffset)
        assertEquals(null, domain.dstUntil)
        assertEquals(19800, domain.rawOffset)
        assertEquals("Asia/Kolkata", domain.timezone)
        assertEquals(1771408200L, domain.unixTime)
        assertEquals("2026-02-18T11:00:00.000000+00:00", domain.utcDatetime)
        assertEquals("+05:30", domain.utcOffset)
        assertEquals(8, domain.weekNumber)
    }

    @Test
    fun `toDomain handles null values correctly`() {
        // Given
        val response = TimeZoneResponse(
            abbreviation = null,
            clientIp = null,
            datetime = null,
            dayOfWeek = null,
            dayOfYear = null,
            dst = null,
            dstFrom = null,
            dstOffset = null,
            dstUntil = null,
            rawOffset = null,
            timezone = null,
            unixtime = null,
            utcDatetime = null,
            utcOffset = null,
            weekNumber = null
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("", domain.abbreviation)
        assertEquals(null, domain.clientIp)
        assertEquals("", domain.datetime)
        assertEquals(0, domain.dayOfWeek)
        assertEquals(0, domain.dayOfYear)
        assertEquals(false, domain.isDst)
        assertEquals(null, domain.dstFrom)
        assertEquals(0, domain.dstOffset)
        assertEquals(null, domain.dstUntil)
        assertEquals(0, domain.rawOffset)
        assertEquals("", domain.timezone)
        assertEquals(0L, domain.unixTime)
        assertEquals(null, domain.utcDatetime)
        assertEquals("", domain.utcOffset)
        assertEquals(0, domain.weekNumber)
    }

    @Test
    fun `toDomain maps empty strings correctly`() {
        // Given
        val response = TimeZoneResponse(
            abbreviation = "",
            datetime = "",
            timezone = "",
            utcOffset = ""
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("", domain.abbreviation)
        assertEquals("", domain.datetime)
        assertEquals("", domain.timezone)
        assertEquals("", domain.utcOffset)
    }
}

