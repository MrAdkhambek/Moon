@file:Suppress(
    "DSL_SCOPE_VIOLATION",
    "UnstableApiUsage"
)

import io.gitlab.arturbosch.detekt.Detekt

plugins {
    // Kotlin plugins
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false

    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)

    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.publish)
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    config = files("config/detekt/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    reports.html.required.set(true)
}
