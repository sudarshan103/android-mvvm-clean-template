package com.arch.data.remote.retrofit.config

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * Gson configuration utility that handles field naming policy
 */
object GsonConfig {
    /**
     * Create a Gson instance with custom field naming strategy
     * Converts snake_case in JSON to camelCase in Kotlin properties and vice versa
     */
    fun createGson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}

