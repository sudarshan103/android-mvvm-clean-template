package com.arch.data.remote.retrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Authentication interceptor that adds auth token to requests when needed
 * Pass null token if auth is not required for current request
 */
class AuthInterceptor(
    private val authToken: String? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Only add auth header if token is provided
        val requestBuilder = originalRequest.newBuilder()
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

