package com.arch.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Base UseCase interface for operations that return a Flow.
 * Use this for reactive operations that emit multiple values over time.
 *
 * @param Params Input parameters for the use case
 * @param Result Output type wrapped in a Flow
 */
abstract class FlowUseCase<in Params, Result> {
    /**
     * Execute the use case with given parameters.
     * @param params Input parameters
     * @return Flow emitting results
     */
    abstract operator fun invoke(params: Params): Flow<Result>
}

/**
 * Base UseCase interface for one-shot operations.
 * Use this for operations that return a single value.
 *
 * @param Params Input parameters for the use case
 * @param Result Output type
 */
abstract class SuspendUseCase<in Params, Result> {
    /**
     * Execute the use case with given parameters.
     * @param params Input parameters
     * @return Single result value
     */
    abstract suspend operator fun invoke(params: Params): Result
}

/**
 * Marker object for use cases that don't require parameters.
 */
object NoParams
