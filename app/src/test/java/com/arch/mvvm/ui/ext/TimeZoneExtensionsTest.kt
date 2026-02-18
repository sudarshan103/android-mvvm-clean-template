package com.arch.mvvm.ui.ext

import com.arch.domain.model.TimeZone
import com.arch.mvvm.base.BaseUnitTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeZoneExtensionsTest : BaseUnitTest() {
    private val mockTimeZone = TimeZone(
        abbreviation = "IST",
        datetime = "2026-02-18T16:00:00.000000+05:30",
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

    @Test
    fun `formatToDisplayDate returns formatted date for valid datetime`() {
        // Given
        val timeZone = mockTimeZone.copy(datetime = "2026-02-18T16:30:45.000000+05:30")

        // When
        val result = timeZone.formatToDisplayDate()

        // Then
        assertEquals("Feb 18, 2026, 04:30PM", result)
    }

    @Test
    fun `formatToDisplayDate returns N_A for empty datetime`() {
        // Given
        val timeZone = mockTimeZone.copy(datetime = "")

        // When
        val result = timeZone.formatToDisplayDate()

        // Then
        assertEquals("N/A", result)
    }

    @Test
    fun `formatToDisplayDate returns N_A for invalid datetime`() {
        // Given
        val timeZone = mockTimeZone.copy(datetime = "invalid-date")

        // When
        val result = timeZone.formatToDisplayDate()

        // Then
        assertEquals("N/A", result)
    }

    @Test
    fun `compareWithLocalTime returns Times match for small difference`() {
        // Given - using current time
        val now = java.time.OffsetDateTime.now()
        val timeZone = mockTimeZone.copy(datetime = now.toString())

        // When
        val (message, matches) = timeZone.compareWithLocalTime()

        // Then
        assertEquals("Times match ✓", message)
        assertTrue(matches)
    }

    @Test
    fun `compareWithLocalTime returns behind message when timezone is earlier`() {
        // Given - 5 hours behind
        val now = java.time.OffsetDateTime.now()
        val earlier = now.minusHours(5)
        val timeZone = mockTimeZone.copy(
            datetime = earlier.toString(),
            timezone = "America/New_York"
        )

        // When
        val (message, matches) = timeZone.compareWithLocalTime()

        // Then
        assertTrue(message.contains("behind"))
        assertFalse(matches)
    }

    @Test
    fun `compareWithLocalTime returns ahead message when timezone is later`() {
        // Given - 5 hours ahead
        val now = java.time.OffsetDateTime.now()
        val later = now.plusHours(5)
        val timeZone = mockTimeZone.copy(
            datetime = later.toString(),
            timezone = "Asia/Tokyo"
        )

        // When
        val (message, matches) = timeZone.compareWithLocalTime()

        // Then
        assertTrue(message.contains("ahead"))
        assertFalse(matches)
    }

    @Test
    fun `compareWithLocalTime returns Unable to compare for empty datetime`() {
        // Given
        val timeZone = mockTimeZone.copy(datetime = "")

        // When
        val (message, matches) = timeZone.compareWithLocalTime()

        // Then
        assertEquals("N/A", message)
        assertFalse(matches)
    }

    @Test
    fun `compareWithLocalTime returns Unable to compare for invalid datetime`() {
        // Given
        val timeZone = mockTimeZone.copy(datetime = "invalid-date")

        // When
        val (message, matches) = timeZone.compareWithLocalTime()

        // Then
        assertEquals("Unable to compare", message)
        assertFalse(matches)
    }

    @Test
    fun `getUtcOffsetString returns offset for non-empty utcOffset`() {
        // Given
        val timeZone = mockTimeZone.copy(utcOffset = "+05:30")

        // When
        val result = timeZone.getUtcOffsetString()

        // Then
        assertEquals("+05:30", result)
    }

    @Test
    fun `getUtcOffsetString returns N_A for empty utcOffset`() {
        // Given
        val timeZone = mockTimeZone.copy(utcOffset = "")

        // When
        val result = timeZone.getUtcOffsetString()

        // Then
        assertEquals("N/A", result)
    }

    @Test
    fun `getAbbreviation returns abbreviation for non-empty value`() {
        // Given
        val timeZone = mockTimeZone.copy(abbreviation = "IST")

        // When
        val result = timeZone.getAbbreviation()

        // Then
        assertEquals("IST", result)
    }

    @Test
    fun `getAbbreviation returns N_A for empty abbreviation`() {
        // Given
        val timeZone = mockTimeZone.copy(abbreviation = "")

        // When
        val result = timeZone.getAbbreviation()

        // Then
        assertEquals("N/A", result)
    }
}

