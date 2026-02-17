# Consumer ProGuard rules for data module
# These rules are automatically applied to consuming modules

# Keep API response models for Gson serialization
-keep class com.arch.data.remote.retrofit.api.** { *; }
-keepclassmembers class com.arch.data.remote.retrofit.api.** {
    <fields>;
    <methods>;
}

# Keep repository implementations
-keep class com.arch.data.repo.** { *; }

# Keep DI modules
-keep class com.arch.data.di.** { *; }
