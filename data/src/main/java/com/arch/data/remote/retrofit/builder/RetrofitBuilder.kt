package com.arch.data.remote.retrofit.builder

import android.content.Context
import android.content.pm.ApplicationInfo
import com.arch.data.remote.retrofit.config.GsonConfig
import com.arch.data.remote.retrofit.config.SslConfig
import com.arch.data.remote.retrofit.interceptor.AuthInterceptor
import com.arch.data.remote.retrofit.interceptor.HttpLoggingInterceptor
import com.arch.data.remote.retrofit.interceptor.InternetCheckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit builder utility class that creates and manages Retrofit instances
 * Supports building Retrofit with or without authentication
 *
 * Usage:
 * val retrofit = RetrofitBuilder(context)
 *     .withAuth("token123")
 *     .build()
 */
class RetrofitBuilder(
    private val context: Context
) {
    private var baseUrl: String = ""
    private var authToken: String? = null
    private val interceptors = mutableListOf<okhttp3.Interceptor>()
    private var timeoutSeconds: Long = 30
    private var customTrustManager: javax.net.ssl.X509TrustManager? = null
    private var usePermissiveMode: Boolean = false

    /**
     * Set the base URL for this Retrofit instance
     */
    fun baseUrl(url: String) =
        apply {
            this.baseUrl = url
        }

    /**
     * Set authentication token for requests
     * If set, token will be added to all requests via Authorization header
     */
    fun withAuth(token: String) =
        apply {
            this.authToken = token
        }

    /**
     * Add a custom interceptor
     */
    fun addInterceptor(interceptor: okhttp3.Interceptor) =
        apply {
            interceptors.add(interceptor)
        }

    /**
     * Set request timeout in seconds
     */
    fun timeout(seconds: Long) =
        apply {
            this.timeoutSeconds = seconds
        }

    /**
     * Enable permissive SSL mode (accepts all certificates)
     * WARNING: Only use for development/testing with self-signed certificates
     * DO NOT use in production as it defeats the purpose of HTTPS
     */
    fun withPermissiveSSL(enable: Boolean = true) =
        apply {
            this.usePermissiveMode = enable
        }

    /**
     * Set custom trust manager for SSL/TLS
     * Use this to handle certificate pinning or custom certificate authorities
     */
    fun withCustomTrustManager(trustManager: javax.net.ssl.X509TrustManager) =
        apply {
            this.customTrustManager = trustManager
        }

    /**
     * Build the Retrofit instance with configured settings
     */
    fun build(): Retrofit {
        require(baseUrl.isNotBlank()) { "Base URL must be set" }

        val httpClientBuilder =
            OkHttpClient
                .Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)

        // Configure SSL/TLS
        configureSSL(httpClientBuilder)

        // Add HTTP logging interceptor for debuggable builds
        val isDebugBuild = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebugBuild) {
            httpClientBuilder.addInterceptor(HttpLoggingInterceptor(isDebug = true))
        }

        // Add internet check interceptor first to fail fast
        httpClientBuilder.addInterceptor(InternetCheckInterceptor(context))

        // Add authentication interceptor if token is provided
        if (authToken != null) {
            httpClientBuilder.addInterceptor(AuthInterceptor(authToken))
        }

        // Add custom interceptors
        interceptors.forEach { interceptor ->
            httpClientBuilder.addInterceptor(interceptor)
        }

        val okHttpClient = httpClientBuilder.build()

        return Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonConfig.createGson()))
            .build()
    }

    /**
     * Configure SSL/TLS settings for the OkHttpClient.
     * Uses TLS 1.2 socket factory for Android 7 (API 24) compatibility.
     */
    private fun configureSSL(httpClientBuilder: OkHttpClient.Builder) {
        when {
            usePermissiveMode -> {
                // Use permissive SSL - accepts all certificates
                val trustManager = SslConfig.createPermissiveTrustManager()
                val sslSocketFactory = SslConfig.createTls12SocketFactory(trustManager)
                httpClientBuilder.sslSocketFactory(sslSocketFactory, trustManager)
                // Also disable hostname verification for permissive mode
                httpClientBuilder.hostnameVerifier { _, _ -> true }
            }

            customTrustManager != null -> {
                // Use custom trust manager (e.g., for certificate pinning)
                val sslSocketFactory = SslConfig.createTls12SocketFactory(customTrustManager!!)
                httpClientBuilder.sslSocketFactory(sslSocketFactory, customTrustManager!!)
            }
            // Otherwise use system default SSL/TLS configuration
        }
    }
}

/**
 * Object to manage Retrofit instances (singleton pattern)
 * Stores instances by key to allow multiple Retrofit configurations
 */
object RetrofitInstanceManager {
    internal val instances = mutableMapOf<String, Retrofit>()
    internal val lock = Any()

    /**
     * Get or create a Retrofit instance
     * @param key Unique identifier for this Retrofit configuration
     * @param factory Lambda to create the Retrofit instance if it doesn't exist
     */
    fun getOrCreate(
        key: String = "default",
        factory: () -> Retrofit
    ): Retrofit =
        synchronized(lock) {
            instances.getOrPut(key) { factory() }
        }

    /**
     * Clear a specific Retrofit instance
     */
    fun clear(key: String = "default") {
        synchronized(lock) {
            instances.remove(key)
        }
    }

    /**
     * Clear all Retrofit instances
     */
    fun clearAll() {
        synchronized(lock) {
            instances.clear()
        }
    }

    /**
     * Get service from Retrofit instance
     */
    inline fun <reified T> getService(
        key: String = "default",
        noinline factory: () -> Retrofit
    ): T = getOrCreate(key, factory).create(T::class.java)
}
