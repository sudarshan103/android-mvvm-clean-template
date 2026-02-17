package com.arch.data.mapper

import com.arch.data.remote.retrofit.api.TimeZoneResponse
import com.arch.domain.model.TimeZone

/**
 * Mapper functions to convert between data layer DTOs and domain models.
 * Keeps the mapping logic centralized and testable.
 */
object TimeZoneMapper {

    /**
     * Maps TimeZoneResponse (DTO) to TimeZone (Domain Model).
     */
    fun TimeZoneResponse.toDomain(): TimeZone {
        return TimeZone(
            abbreviation = abbreviation.orEmpty(),
            datetime = datetime.orEmpty(),
            timezone = timezone.orEmpty(),
            utcOffset = utcOffset.orEmpty(),
            dayOfWeek = dayOfWeek ?: 0,
            dayOfYear = dayOfYear ?: 0,
            weekNumber = weekNumber ?: 0,
            unixTime = unixtime ?: 0L,
            isDst = dst ?: false,
            dstOffset = dstOffset ?: 0,
            rawOffset = rawOffset ?: 0,
            clientIp = clientIp,
            dstFrom = dstFrom,
            dstUntil = dstUntil,
            utcDatetime = utcDatetime
        )
    }
}

