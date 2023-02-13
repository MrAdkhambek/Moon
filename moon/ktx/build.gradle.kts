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
    api(projects.moon.moon)
}
