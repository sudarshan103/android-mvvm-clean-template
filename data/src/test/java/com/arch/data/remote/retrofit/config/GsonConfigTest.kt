package com.arch.data.remote.retrofit.config

import com.arch.data.base.BaseUnitTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GsonConfigTest : BaseUnitTest() {
    @Test
    fun `createGson creates non-null Gson instance`() {
        // When
        val gson = GsonConfig.createGson()

        // Then
        assertNotNull(gson)
    }

    @Test
    fun `createGson uses LOWER_CASE_WITH_UNDERSCORES field naming policy`() {
        // When
        val gson = GsonConfig.createGson()

        // Then
        // Test that snake_case is properly handled
        data class TestData(val testValue: String)
        val json = """{"test_value":"hello"}"""
        val result = gson.fromJson(json, TestData::class.java)
        assertEquals("hello", result.testValue)
    }

    @Test
    fun `createGson serializes camelCase to snake_case`() {
        // When
        val gson = GsonConfig.createGson()

        // Then
        data class TestData(val testValue: String)
        val obj = TestData("world")
        val json = gson.toJson(obj)
        assertEquals("""{"test_value":"world"}""", json)
    }
}

