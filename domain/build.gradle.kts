plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    detektPlugins(libs.detekt.formatting)
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

// Disable ktlint on Kotlin scripts to avoid parsing issues
// Prefer disabling the tasks entirely so they do not fail the build.
tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask>().configureEach {
    if (name.contains("KotlinScripts")) {
        enabled = false
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

kover {
    reports {
        filters {
            excludes {
                classes(
                    // Repository interfaces (no implementation)
                    "*.repository.*",

                    // Base UseCase interfaces
                    "*.usecase.FlowUseCase",
                    "*.usecase.SuspendUseCase",
                    "*.usecase.NoParams",

                    // Exception classes (simple data classes)
                    "*.exception.*Exception",

                    // Data classes with no logic
                    "*.model.TimeZone"
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

