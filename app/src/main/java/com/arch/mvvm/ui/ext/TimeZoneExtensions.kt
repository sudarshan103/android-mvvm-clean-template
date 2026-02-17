package com.arch.mvvm.ui.ext

import com.arch.domain.model.TimeZone
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Extension functions for TimeZone domain model formatting and comparison.
 */

/**
 * Format the datetime string to display format.
 * Format: "Feb 16, 2026, 01:40pm"
 */
fun TimeZone.formatToDisplayDate(): String {
    return try {
        if (datetime.isEmpty()) return "N/A"
        val offsetDateTime = OffsetDateTime.parse(datetime)
        val localDateTime = offsetDateTime.toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mma")
        localDateTime.format(formatter)
    } catch (_: Exception) {
        "N/A"
    }
}

/**
 * Compare timezone time with device local time.
 * @return Pair<String, Boolean> where String is the comparison message
 *         and Boolean indicates if times match (within 1 minute tolerance)
 */
fun TimeZone.compareWithLocalTime(): Pair<String, Boolean> {
    return try {
        if (datetime.isEmpty()) return Pair("N/A", false)

        val timezoneTime = OffsetDateTime.parse(datetime)
        val deviceTime = OffsetDateTime.now()

        val diffMinutes = kotlin.math.abs(
            timezoneTime.toInstant().epochSecond - deviceTime.toInstant().epochSecond
        ) / 60

        val diffHours = diffMinutes / 60
        val diffMinutesRemainder = diffMinutes % 60

        val timesMatch = diffMinutes <= 1

        val message = when {
            timesMatch -> "Times match ✓"
            timezoneTime.isBefore(deviceTime) -> {
                val tzName = timezone.ifEmpty { "Timezone" }
                if (diffMinutesRemainder == 0L) {
                    "$tzName is behind by $diffHours hours"
                } else {
                    "$tzName is behind by $diffHours hours $diffMinutesRemainder minutes"
                }
            }
            else -> {
                val tzName = timezone.ifEmpty { "Timezone" }
                if (diffMinutesRemainder == 0L) {
                    "$tzName is ahead by $diffHours hours"
                } else {
                    "$tzName is ahead by $diffHours hours $diffMinutesRemainder minutes"
                }
            }
        }

        Pair(message, timesMatch)
    } catch (_: Exception) {
        Pair("Unable to compare", false)
    }
}

/**
 * Get UTC offset string.
 */
fun TimeZone.getUtcOffsetString(): String {
    return utcOffset.ifEmpty { "N/A" }
}

/**
 * Get timezone abbreviation.
 */
fun TimeZone.getAbbreviation(): String {
    return abbreviation.ifEmpty { "N/A" }
}

