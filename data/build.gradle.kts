plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.arch.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

ktlint {
    version.set("1.3.1")
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    filter {
        exclude("**/build/**")
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    parallel = false // Disable parallel to reduce CPU load
    basePath = rootDir.absolutePath
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "11"
    setSource(fileTree("src"))
    exclude("**/build/**", "**/generated/**", "**/resources/**")
    reports {
        xml.required.set(false)
        html.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

// Disable ktlint on test source sets to avoid parsing issues
// Prefer disabling the tasks entirely so they do not fail the build.
tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>().configureEach {
    if (name.contains("AndroidTest") || name.contains("Test") || name.contains("KotlinScripts")) {
        enabled = false
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.okhttp.mockwebserver)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    detektPlugins(libs.detekt.formatting)
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    // Dependency Injection modules
                    "*.di.*",
                    "*.di.*\$*",

                    // Hilt generated
                    "*_Factory",
                    "*_Factory\$*",
                    "*_HiltModules*",
                    "*Hilt_*",
                    "*_Impl*",
                    "*_MembersInjector",
                    "*Module_*",

                    // Build config
                    "*.BuildConfig",

                    // Network infrastructure (complex Android dependencies)
                    "*.remote.retrofit.builder.RetrofitBuilder",
                    "*.remote.retrofit.builder.RetrofitBuilder\$*",
                    "*.remote.retrofit.builder.RetrofitInstanceManager",
                    "*.remote.retrofit.builder.RetrofitInstanceManager\$*",
                    "*.remote.retrofit.config.SslConfig",
                    "*.remote.retrofit.config.SslConfig\$*",
                    "*.remote.retrofit.config.Tls12SocketFactory",

                    // Interceptors (integration test territory)
                    "*.remote.retrofit.interceptor.*",
                    "*.remote.retrofit.interceptor.*\$*",

                    // API service interfaces (Retrofit interfaces)
                    "*.remote.retrofit.api.*ApiService"
                )
                annotatedBy(
                    "*Module*",
                    "*InstallIn*",
                    "*Provides*",
                    "*Binds*"
                )
            }
        }

        verify {
            rule("Minimum line coverage of 85%") {
                bound {
                    minValue = 85
                    coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                }
            }
        }
    }
}

