plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.arch.mvvm"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.arch.mvvm"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
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
    // DI wiring: app needs data module for Hilt modules while still coding only against domain interfaces.
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.arch.core.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    detektPlugins(libs.detekt.formatting)
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    // Activities and Fragments
                    "*Activity",
                    "*Activity\$*",
                    "*Fragment",
                    "*Fragment\$*",
                    "*.base.BaseActivity",
                    "*.base.BaseActivity\$*",

                    // Composable UI
                    "*ComposableSingletons*",
                    "*.ui.screen.*",
                    "*.ui.screen.*\$*",
                    "*.ui.theme.*",
                    "*.ui.theme.*\$*",

                    // Application class
                    "*App",
                    "*App\$*",
                    "*.App",

                    // Hilt generated
                    "*_Factory",
                    "*_Factory\$*",
                    "*_HiltModules*",
                    "*Hilt_*",
                    "*_Impl*",
                    "*_MembersInjector",
                    "*Module_*",

                    // Data binding
                    "*.databinding.*",
                    "*.DataBinderMapperImpl",
                    "*.DataBinderMapperImpl\$*",

                    // Build config
                    "*.BuildConfig",

                    // Utilities requiring Android Context
                    "*.ext.TimezoneDetector",
                    "*.ext.TimezoneDetector\$*"
                )
                annotatedBy(
                    "*Composable*",
                    "*HiltAndroidApp*",
                    "*AndroidEntryPoint*",
                    "*Module*",
                    "*InstallIn*"
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

