package me.adkhambek.app

import org.gradle.api.JavaVersion

internal object Config {
    val javaVersion = JavaVersion.VERSION_11

    val freeCompilerArgs = listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xexplicit-api=strict",
        "-Xjvm-default=all",
    )

    val libraryCompilerArgs = listOf(
        "-Xexplicit-api=strict",
    )
}
