// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    id("org.sonarqube") version "4.4.1.3373"
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val javapoetVersion = libs.findVersion("javapoet").get().requiredVersion

subprojects {
    configurations.configureEach {
        resolutionStrategy.force("com.squareup:javapoet:$javapoetVersion")
    }
}

// SonarCloud Configuration
sonarqube {
    properties {
        // Project identification - UPDATE THESE WITH YOUR ACTUAL SONARCLOUD PROJECT INFO
        property("sonar.projectKey", "dummy-project-key")
        property("sonar.projectName", "MVVM Clean Template")
        property("sonar.projectVersion", "1.0.0")
        property("sonar.organization", "dummy-organization")

        // SonarCloud settings
        property("sonar.host.url", "https://sonarcloud.io")

        // Authentication token - Use environment variable SONAR_LOGIN for security
        // For local setup: export SONAR_LOGIN=your_token && ./gradlew sonarqube
        // For CI/CD: Pass via environment variable, never hardcode tokens
        // Fallback token if needed (NOT RECOMMENDED for shared repos):
        // property("sonar.login", "")

        // Source and binary paths
        property("sonar.sources", "app/src/main/java,data/src/main/java,domain/src/main/java")
        property("sonar.java.binaries", "app/build/intermediates/classes/debug,data/build/intermediates/classes/debug,domain/build/intermediates/classes/debug")

        // Language support
        property("sonar.language", "java,kotlin")

        // Test and coverage exclusions
        property("sonar.coverage.exclusions", "**/test/**,**/androidTest/**,**/*Test.kt,**/*Test.java")

        // Detekt integration for Kotlin code quality
        property("sonar.kotlin.detekt.reportPaths", "app/build/reports/detekt/detekt.html")

        // KtLint integration for code style and formatting
        // Reports from KtLint are sent to SonarCloud for style analysis
        property("sonar.kotlin.ktlint.reportPaths", "app/build/reports/ktlint/ktlintMainSourceSetCheck/ktlintMainSourceSetCheck.txt")

        // Optional: Code coverage (uncomment and configure if using JaCoCo)
        // property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/coverage.xml")
    }
}
