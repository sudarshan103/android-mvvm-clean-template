package com.arch.domain.usecase

import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import com.arch.domain.repository.TimeZoneRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for fetching timezone data.
 * Encapsulates the business logic for timezone retrieval operations.
 */
class GetTimeZoneUseCase(
    private val repository: TimeZoneRepository
) : FlowUseCase<GetTimeZoneUseCase.Params, Result<TimeZone>>() {
    override fun invoke(params: Params): Flow<Result<TimeZone>> =
        repository.getTimeZone(
            timezone = params.timezone,
            showProgress = params.showProgress
        )

    /**
     * Parameters for GetTimeZoneUseCase.
     * @property timezone The timezone identifier (e.g., "UTC", "Asia/Kolkata")
     * @property showProgress Whether to show loading indicator
     */
    data class Params(
        val timezone: String,
        val showProgress: Boolean = true
    )

    companion object {
        const val UTC = "UTC"
    }
}
