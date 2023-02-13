@file:Suppress(
    "DSL_SCOPE_VIOLATION",
    "UnstableApiUsage"
)

plugins {
    id("com.adkhambek.kotlin")
    id("com.adkhambek.publish")
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.java)
    compileOnly(projects.moon.moon)

    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.coroutines.core)
    compileOnly(libs.google.findbugs.jsr305)
}
