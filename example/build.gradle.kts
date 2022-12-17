@file:Suppress(
    "DSL_SCOPE_VIOLATION",
    "UnstableApiUsage"
)
plugins {
    id("me.adkhambek.kotlin")
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation("me.adkhambek.moon:moon-ktx:0.0.6-alpha")
    implementation("me.adkhambek.moon:convertor-kotlin-serialization:0.0.6-alpha")
    implementation("me.adkhambek.moon:moon-throwable:0.0.6-alpha")


//    implementation(projects.moon.ktx)
//    implementation(projects.moon.throwable)
//    implementation(projects.convertors.kotlinxSerialization)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization.json)

    implementation(platform(libs.squareup.okhttp.bom))
    implementation(libs.squareup.okhttp.logging)
    implementation(libs.squareup.okhttp.okhttp)
}
