import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-library`
    `maven-publish`
}

dependencies {
    compileOnly(Deps.Kotlin.stdLib)
    compileOnly(Deps.KotlinX.coroutinesCore)

    compileOnly(Deps.Google.findbugs)

    compileOnly(Deps.Squareup.okhttp3)
    compileOnly(Deps.Squareup.logging)

    api(Deps.SocketIO.client)
//    {
//        exclude(group = "org.json", module = "json")
//    }

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

tasks.withType<KotlinCompile> {
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
            artifactId = "moon"
            version = "alpha-0.0.1"
        }
    }
}
