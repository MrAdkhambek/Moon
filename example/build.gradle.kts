plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

group = "mr.adkhambek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
//    mavenLocal()
}

dependencies {
    implementation("me.adkhambek.moon:moon:alpha-0.0.1")
    implementation("me.adkhambek.moon:convertor-kotlin-serialization:alpha-0.0.1")

//    implementation(projects.moon)
//    implementation(projects.convertors.kotlinxSerialization)

    implementation(Deps.Kotlin.stdLib)
    implementation(Deps.KotlinX.coroutinesCore)
    implementation(Deps.KotlinX.serializationJson)

    implementation(Deps.Squareup.okhttp3)
    implementation(Deps.Squareup.logging)

    testImplementation(Deps.Test.junitAPI)
    testRuntimeOnly(Deps.Test.junitEngine)
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}