package com.arch.data.remote.retrofit.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor that checks for internet connectivity
 * Throws InternetNotEnabledException if no network is available
 */
class InternetCheckInterceptor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable(context)) {
            throw IOException("No internet connection available")
        }
        return chain.proceed(chain.request())
    }

    @SuppressLint("MissingPermission")
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
