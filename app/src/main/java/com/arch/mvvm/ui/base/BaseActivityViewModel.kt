package com.arch.mvvm.ui.base

import androidx.lifecycle.ViewModel
import com.arch.domain.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Standalone ViewModel for managing loading state across Activities.
 * Observes Result objects from domain layer to manage progress dialog visibility.
 * BaseActivity manages this ViewModel and handles progress dialog visibility.
 */
class BaseActivityViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Update loading state based on Result.
     * Call this when receiving results to automatically manage loading state.
     * @param result The Result from domain layer
     */
    fun handleLoadingState(result: Result<*>) {
        _isLoading.value =
            when (result) {
                is Result.Loading -> result.showProgress
                is Result.Success -> false
                is Result.Error -> false
            }
    }
}
