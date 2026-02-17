package com.arch.data.di

import android.content.Context
import com.arch.data.remote.retrofit.api.TimeZoneApiService
import com.arch.data.remote.retrofit.builder.RetrofitBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Hilt dependency injection module for providing network-related dependencies
 * Ensures single instances of Retrofit and API services across the application
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Provide TimeZone API Retrofit instance
     * This endpoint doesn't require authentication
     * Enables permissive SSL mode for development to handle self-signed certificates
     */
    @Provides
    @Singleton
    fun provideTimeZoneRetrofit(
        @ApplicationContext context: Context
    ): Retrofit =
        RetrofitBuilder(context)
            .baseUrl("https://time.now")
            .withPermissiveSSL(enable = true) // For development/testing only
            .build()

    /**
     * Provide TimeZone API Service
     */
    @Provides
    @Singleton
    fun provideTimeZoneApiService(retrofit: Retrofit): TimeZoneApiService =
        retrofit.create(TimeZoneApiService::class.java)
}
