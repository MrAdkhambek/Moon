buildscript {
    extra["kotlinVersion"] = Deps.Kotlin.version
    val kotlinVersion: String by extra

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    group = "com.github.MrAdkhambek"
    version = "alpha-0.0.1"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}