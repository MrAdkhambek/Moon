@file:Suppress(
    "UnstableApiUsage",
)

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

private const val SNAPSHOT = "SNAPSHOT"
private const val USER_NAME = "mavenCentralUsername"
private const val USER_PASS = "mavenCentralPassword"
private const val URL_SNAPSHOT = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
private const val URL_STAGING = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"

class PublisherPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.vanniktech.maven.publish")
                apply("org.jetbrains.dokka")
            }

            extensions.configure<PublishingExtension> {
                publications {
                    repositories {
                        maven {
                            url = if (version.toString().endsWith(SNAPSHOT)) uri(URL_SNAPSHOT) else uri(URL_STAGING)
                            credentials {
                                username = getLocalProperty(USER_NAME).toString()
                                password = getLocalProperty(USER_PASS).toString()
                            }
                        }
                    }
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                add("dokkaHtmlPlugin", libs.findLibrary("dokka-java").get())
            }
        }
    }

    private fun Project.getLocalProperty(key: String, file: String = "local.properties"): Any {
        val properties = Properties()
        val localProperties = File(rootDir, file)

        if (localProperties.isFile) {
            InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
                properties.load(reader)
            }
        } else {
            error("File from not found")
        }

        return properties.getProperty(key)
    }
}
