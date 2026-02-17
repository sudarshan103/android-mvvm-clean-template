package com.arch.data.remote.retrofit.handler

import com.arch.data.remote.model.ApiResponse
import com.arch.domain.exception.ApiException
import com.arch.domain.exception.HttpException
import com.arch.domain.exception.InternetNotEnabledException
import com.arch.domain.exception.NetworkIOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException

/**
 * Handler for API calls with comprehensive error handling
 * Converts Retrofit responses to ApiResponse sealed class
 * Supports progress tracking via Flow
 */
object ApiCallHandler {
    /**
     * Execute an API call and emit results as Flow
     * @param T The type of the response body
     * @param showProgress Whether to emit progress loading states
     * @param apiCall Suspend function that executes the API call
     * @return Flow emitting ApiResponse with success/error/exception states
     */
    fun <T> executeApiCall(
        showProgress: Boolean = true,
        apiCall: suspend () -> Response<T>,
    ): Flow<ApiResponse<T>> =
        flow {
            try {
                if (showProgress) {
                    emit(ApiResponse.Loading(showProgress = true))
                }

                val response = apiCall()

                if (showProgress) {
                    emit(ApiResponse.Loading(showProgress = false))
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        emit(ApiResponse.Success(body))
                    } else {
                        emit(
                            ApiResponse.Error(
                                statusCode = response.code(),
                                errorBody = "Response body is empty",
                                exception =
                                    HttpException(
                                        code = response.code(),
                                        message = "Response body is empty"
                                    )
                            )
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    emit(
                        ApiResponse.Error(
                            statusCode = response.code(),
                            errorBody = errorBody,
                            exception =
                                HttpException(
                                    code = response.code(),
                                    message = "HTTP ${response.code()}: ${response.message()}",
                                    errorBody = errorBody
                                )
                        )
                    )
                }
            } catch (e: InternetNotEnabledException) {
                if (showProgress) {
                    emit(ApiResponse.Loading(showProgress = false))
                }
                emit(ApiResponse.Exception(e as ApiException))
            } catch (e: IOException) {
                if (showProgress) {
                    emit(ApiResponse.Loading(showProgress = false))
                }
                emit(
                    ApiResponse.Exception(
                        NetworkIOException(
                            message = e.message ?: "Network IO error",
                            cause = e
                        )
                    )
                )
            } catch (e: Exception) {
                if (showProgress) {
                    emit(ApiResponse.Loading(showProgress = false))
                }
                emit(
                    ApiResponse.Exception(
                        ApiException(
                            message = e.message ?: "Unknown error",
                            cause = e
                        )
                    )
                )
            }
        }

    /**
     * Execute an API call without emitting progress states
     * Useful for quick fire-and-forget API calls or when manually managing progress
     */
    fun <T> executeApiCallSilent(apiCall: suspend () -> Response<T>): Flow<ApiResponse<T>> =
        executeApiCall(showProgress = false, apiCall)
}
