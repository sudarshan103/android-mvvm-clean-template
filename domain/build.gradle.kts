plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
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

// Run ktlint and detekt manually with: ./gradlew ktlintCheck detekt
// This prevents automatic execution during builds which can freeze the system
// tasks.matching { it.name == "check" }.configureEach {
//     dependsOn("ktlintCheck", "detekt")
// }
