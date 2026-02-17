package com.arch.data.repo

import com.arch.data.mapper.TimeZoneMapper.toDomain
import com.arch.data.remote.model.ApiResponse
import com.arch.data.remote.retrofit.api.TimeZoneApiService
import com.arch.data.remote.retrofit.api.TimeZoneResponse
import com.arch.data.remote.retrofit.handler.ApiCallHandler
import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import com.arch.domain.repository.TimeZoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository implementation for TimeZone API operations.
 * Implements domain layer interface following Dependency Inversion Principle.
 * Converts data layer responses to domain layer Result types.
 */
class TimeZoneRepositoryImpl @Inject constructor(
    private val apiService: TimeZoneApiService
) : TimeZoneRepository {

    override fun getTimeZone(timezone: String, showProgress: Boolean): Flow<Result<TimeZone>> {
        return ApiCallHandler.executeApiCall(showProgress = showProgress) {
            apiService.getTimeZone(timezone)
        }.map { response ->
            mapToResult(response)
        }
    }

    override fun getUtcTime(showProgress: Boolean): Flow<Result<TimeZone>> {
        return getTimeZone(UTC_TIMEZONE, showProgress)
    }

    private fun mapToResult(response: ApiResponse<TimeZoneResponse>): Result<TimeZone> {
        return when (response) {
            is ApiResponse.Success -> Result.Success(response.data.toDomain())
            is ApiResponse.Error -> Result.Error(response.exception)
            is ApiResponse.Exception -> Result.Error(response.exception)
            is ApiResponse.Loading -> Result.Loading(response.showProgress)
        }
    }

    companion object {
        private const val UTC_TIMEZONE = "UTC"
    }
}

