package com.arch.mvvm.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Base Activity class that handles progress dialog management
 * Automatically shows/hides progress dialog based on loading states
 * Manages its own BaseActivityViewModel for loading state observation
 */
open class BaseActivity : ComponentActivity() {

    private var progressDialog: AlertDialog? = null
    protected val baseActivityViewModel: BaseActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLoadingStateObservation()
    }

    /**
     * Setup automatic loading state observation from BaseActivityViewModel
     * Called during onCreate to automatically handle progress dialog visibility
     */
    private fun setupLoadingStateObservation() {
        lifecycleScope.launch {
            baseActivityViewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    showProgressDialog()
                } else {
                    hideProgressDialog()
                }
            }
        }
    }

    /**
     * Show indeterminate progress dialog
     * @param message Message to display (optional)
     */
    protected fun showProgressDialog(message: String = "Loading...") {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            progressDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Please wait")
                .setMessage(message)
                .setCancelable(false)
                .show()
        }
    }

    /**
     * Hide progress dialog if it's showing
     */
    protected fun hideProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    /**
     * Observe a state flow in the lifecycle
     * Automatically cancels collection when activity is destroyed
     */
    protected inline fun <reified T> observeState(
        stateFlow: kotlinx.coroutines.flow.StateFlow<T>,
        crossinline action: suspend (T) -> Unit
    ) {
        lifecycleScope.launch {
            stateFlow.collect { state ->
                action(state)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.dismiss()
        progressDialog = null
    }
}
