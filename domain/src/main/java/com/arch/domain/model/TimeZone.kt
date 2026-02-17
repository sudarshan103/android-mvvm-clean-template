package com.arch.domain.model

/**
 * Domain model for TimeZone data.
 * This is a pure Kotlin data class with no Android or framework dependencies.
 */
data class TimeZone(
    val abbreviation: String,
    val datetime: String,
    val timezone: String,
    val utcOffset: String,
    val dayOfWeek: Int,
    val dayOfYear: Int,
    val weekNumber: Int,
    val unixTime: Long,
    val isDst: Boolean,
    val dstOffset: Int,
    val rawOffset: Int,
    val clientIp: String? = null,
    val dstFrom: String? = null,
    val dstUntil: String? = null,
    val utcDatetime: String? = null
)

