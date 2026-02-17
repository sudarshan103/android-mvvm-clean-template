package com.arch.mvvm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import com.arch.domain.usecase.GetTimeZoneUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for TimeZone operations.
 * Demonstrates clean architecture with UseCase pattern.
 * Loading state is managed by BaseActivity through BaseActivityViewModel.
 */
@HiltViewModel
class TimeZoneViewModel @Inject constructor(
    private val getTimeZoneUseCase: GetTimeZoneUseCase
) : ViewModel() {

    private val _utcTimeState = MutableStateFlow<Result<TimeZone>?>(null)
    val utcTimeState: StateFlow<Result<TimeZone>?> = _utcTimeState.asStateFlow()

    private val _timeZoneState = MutableStateFlow<Result<TimeZone>?>(null)
    val timeZoneState: StateFlow<Result<TimeZone>?> = _timeZoneState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Fetch time for specific timezone.
     * @param timezone The timezone identifier (e.g., "UTC", "Asia/Kolkata")
     */
    fun fetchTimeZone(timezone: String) {
        viewModelScope.launch {
            val params = GetTimeZoneUseCase.Params(
                timezone = timezone,
                showProgress = true
            )
            getTimeZoneUseCase(params).collect { result ->
                handleResult(result, isUtc = timezone == UTC)
            }
        }
    }

    private fun handleResult(result: Result<TimeZone>, isUtc: Boolean) {
        if (isUtc) {
            _utcTimeState.value = result
        } else {
            _timeZoneState.value = result
        }

        when (result) {
            is Result.Success -> _errorMessage.value = null
            is Result.Error -> _errorMessage.value = result.exception.message ?: "Unknown error occurred"
            is Result.Loading -> Unit
        }
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    companion object {
        const val UTC = "UTC"
    }
}
