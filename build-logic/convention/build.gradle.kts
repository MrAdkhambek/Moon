plugins {
    `kotlin-dsl`
}

group = "com.adkhambek.app.buildlogic"

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
            id = "com.adkhambek.kotlin"
            implementationClass = "LibraryKotlinConventionPlugin"
        }
    }
}
