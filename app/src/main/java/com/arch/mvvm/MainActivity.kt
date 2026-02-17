package com.arch.mvvm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.arch.mvvm.ui.TimeZoneViewModel
import com.arch.mvvm.ui.base.BaseActivity
import com.arch.mvvm.ui.screen.TimeZoneScreen
import com.arch.mvvm.ui.theme.MvvmCleanTemplateTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point of the application.
 * Demonstrates clean architecture with MVVM pattern.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val viewModel: TimeZoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupLoadingObservers()
        setupContent()
    }

    private fun setupLoadingObservers() {
        observeState(viewModel.utcTimeState) { state ->
            state?.let { baseActivityViewModel.handleLoadingState(it) }
        }
        observeState(viewModel.timeZoneState) { state ->
            state?.let { baseActivityViewModel.handleLoadingState(it) }
        }
    }

    private fun setupContent() {
        setContent {
            MvvmCleanTemplateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TimeZoneScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
