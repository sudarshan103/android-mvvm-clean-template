package com.arch.data.remote.retrofit.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * TimeZone API service
 * Base URL: https://time.now/
 */
interface TimeZoneApiService {
    /**
     * Get current time for a specific timezone
     * @param timezone The timezone path (e.g., UTC, Asia/Kolkata)
     * Endpoint: /developer/api/timezone/{timezone}
     */
    @GET("/developer/api/timezone/{timezone}")
    suspend fun getTimeZone(
        @Path("timezone") timezone: String
    ): Response<TimeZoneResponse>
}

/**
 * Time Zone API Response model
 * Properties use snake_case in API but will be converted to camelCase by Gson
 */
data class TimeZoneResponse(
    val abbreviation: String? = null,
    val clientIp: String? = null,
    val datetime: String? = null,
    val dayOfWeek: Int? = null,
    val dayOfYear: Int? = null,
    val dst: Boolean? = null,
    val dstFrom: String? = null,
    val dstOffset: Int? = null,
    val dstUntil: String? = null,
    val rawOffset: Int? = null,
    val timezone: String? = null,
    val unixtime: Long? = null,
    val utcDatetime: String? = null,
    val utcOffset: String? = null,
    val weekNumber: Int? = null
)
