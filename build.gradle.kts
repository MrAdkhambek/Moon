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

        classpath(Deps.Publish.dokkaPlugin)
        classpath(Deps.Publish.mavenPublishPlugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "me.adkhambek.moon"
    version = "alpha-0.0.1"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

