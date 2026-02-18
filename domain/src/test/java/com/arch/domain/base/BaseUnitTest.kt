package com.arch.domain.base

import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before

/**
 * Base class for unit tests in domain module.
 * Provides common test infrastructure and MockK lifecycle management.
 */
abstract class BaseUnitTest {
    /**
     * Called before each test.
     * Override this method to add test-specific setup logic.
     */
    @Before
    open fun setUp() {
        // Override in subclasses for test-specific setup
    }

    /**
     * Called after each test.
     * Cleans up mocks and resources.
     */
    @After
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
        onTearDown()
    }

    /**
     * Override this method to add test-specific cleanup logic.
     */
    open fun onTearDown() {
        // Override in subclasses for test-specific cleanup
    }
}
