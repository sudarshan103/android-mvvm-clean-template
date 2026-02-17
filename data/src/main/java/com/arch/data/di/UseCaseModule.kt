package com.arch.data.di

import com.arch.domain.repository.TimeZoneRepository
import com.arch.domain.usecase.GetTimeZoneUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt dependency injection module for providing use case instances.
 * Use cases are scoped to ViewModelComponent as they are typically used within ViewModels.
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideGetTimeZoneUseCase(repository: TimeZoneRepository): GetTimeZoneUseCase = GetTimeZoneUseCase(repository)
}
