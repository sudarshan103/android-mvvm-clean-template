package com.arch.mvvm.ui.base

import app.cash.turbine.test
import com.arch.domain.exception.ApiException
import com.arch.domain.model.Result
import com.arch.mvvm.base.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseActivityViewModelTest : BaseViewModelTest() {
    private lateinit var viewModel: BaseActivityViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = BaseActivityViewModel()
    }

    @Test
    fun `initial loading state is false`() = runTest {
        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `handleLoadingState with Loading showProgress true sets isLoading to true`() = runTest {
        // Given
        val result: Result<String> = Result.Loading(showProgress = true)

        // When
        viewModel.handleLoadingState(result)

        // Then
        viewModel.isLoading.test {
            assertTrue(awaitItem())
        }
    }

    @Test
    fun `handleLoadingState with Loading showProgress false sets isLoading to false`() = runTest {
        // Given
        val result: Result<String> = Result.Loading(showProgress = false)

        // When
        viewModel.handleLoadingState(result)

        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `handleLoadingState with Success sets isLoading to false`() = runTest {
        // Given
        val result: Result<String> = Result.Success("test data")

        // When
        viewModel.handleLoadingState(result)

        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `handleLoadingState with Error sets isLoading to false`() = runTest {
        // Given
        val result: Result<String> = Result.Error(ApiException("Error occurred"))

        // When
        viewModel.handleLoadingState(result)

        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem())
        }
    }

    @Test
    fun `handleLoadingState sequence Loading true then Success then Loading false works correctly`() = runTest {
        // When & Then
        viewModel.isLoading.test {
            assertEquals(false, awaitItem()) // Initial state

            val loadingResult: Result<String> = Result.Loading(showProgress = true)
            viewModel.handleLoadingState(loadingResult)
            assertEquals(true, awaitItem())

            viewModel.handleLoadingState(Result.Success("data"))
            assertEquals(false, awaitItem())
        }
    }
}

