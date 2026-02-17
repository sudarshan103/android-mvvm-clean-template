package com.arch.domain.repository

import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for TimeZone operations.
 * Defined in domain layer to follow Dependency Inversion Principle.
 * Implementation resides in data layer.
 */
interface TimeZoneRepository {

    /**
     * Get current time for a specific timezone.
     * @param timezone The timezone identifier (e.g., "UTC", "Asia/Kolkata", "America/New_York")
     * @param showProgress Whether to emit loading states
     * @return Flow emitting Result with TimeZone data
     */
    fun getTimeZone(timezone: String, showProgress: Boolean = true): Flow<Result<TimeZone>>

    /**
     * Get current UTC time.
     * @param showProgress Whether to emit loading states
     * @return Flow emitting Result with TimeZone data
     */
    fun getUtcTime(showProgress: Boolean = true): Flow<Result<TimeZone>>
}

