@file:Suppress(
    "DSL_SCOPE_VIOLATION",
    "UnstableApiUsage"
)

plugins {
    id("com.adkhambek.kotlin")
    id("com.adkhambek.publish")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.java)
    compileOnly(libs.kotlin.stdlib)

    compileOnly(projects.moon.moon)
    compileOnly(libs.kotlin.serialization.json)
}