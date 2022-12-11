@file:Suppress(
    "DSL_SCOPE_VIOLATION",
    "UnstableApiUsage"
)
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    id("me.adkhambek.kotlin")
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.java)
    compileOnly(libs.kotlin.stdlib)

    compileOnly(projects.moon)
    compileOnly(libs.kotlin.serialization.json)
}

publishing {
    publications {
        repositories {
            maven {
                val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
                credentials {
                    username = getLocalProperty("mavenCentralUsername").toString()
                    password = getLocalProperty("mavenCentralPassword").toString()
                }
            }
        }
    }
}

fun Project.getLocalProperty(key: String, file: String = "local.properties"): Any {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found")

    return properties.getProperty(key)
}
