package com.arch.data.di

import com.arch.data.repo.TimeZoneRepositoryImpl
import com.arch.domain.repository.TimeZoneRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for providing repository instances.
 * Binds repository implementations to their domain interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTimeZoneRepository(
        impl: TimeZoneRepositoryImpl
    ): TimeZoneRepository
}

