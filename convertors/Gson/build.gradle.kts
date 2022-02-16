plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(projects.moon)

    compileOnly(Deps.Kotlin.stdLib)
    compileOnly(Deps.KotlinX.coroutinesCore)

    compileOnly(Deps.Google.gson)

    compileOnly(Deps.Squareup.okhttp3)
    compileOnly(Deps.Squareup.logging)

    testImplementation(Deps.Test.junitAPI)
    testRuntimeOnly(Deps.Test.junitEngine)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}



val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar.get())

            groupId = "com.github.MrAdkhambek"
            artifactId = "moon-convertor-gson"
            version = "alpha-0.0.1"
        }
    }
}