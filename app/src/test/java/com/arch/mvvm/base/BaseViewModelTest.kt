package com.arch.mvvm.base

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Base class for ViewModel unit tests.
 * Provides coroutine test dispatcher and LiveData testing infrastructure.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest : BaseUnitTest() {
    /**
     * Rule to execute LiveData operations synchronously
     */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     * Test dispatcher for coroutines
     */
    protected lateinit var testDispatcher: TestDispatcher

    @Before
    override fun setUp() {
        super.setUp()
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    override fun onTearDown() {
        Dispatchers.resetMain()
    }
}

