plugins {
    `kotlin-dsl`
}

group = "me.adkhambek.app.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinLibrary") {
            id = "me.adkhambek.kotlin"
            implementationClass = "LibraryKotlinConventionPlugin"
        }
    }
}
