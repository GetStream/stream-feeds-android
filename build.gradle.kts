import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.getstream.feeds.android.Configuration
import java.io.FileNotFoundException
import java.util.Calendar

apply(from = "${rootDir}/gradle/scripts/sonar.gradle")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.stream.android.library) apply false
    alias(libs.plugins.stream.android.application) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.arturbosch.detekt) apply true
    alias(libs.plugins.spotless) apply true
    id("com.google.gms.google-services") version "4.4.3" apply false
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kover)
}

spotless {
    kotlin {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktfmt().kotlinlangStyle()
        licenseHeaderFile(file("./config/license/generated/license-$currentYear.txt"))
    }
}

detekt {
    autoCorrect = true
    toolVersion = "1.23.8"
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

// License tasks
subprojects {
    apply(from = "${rootDir}/gradle/scripts/coverage.gradle")

    tasks.register("generateLicense") {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        val licenseTemplate = file("../config/license/license.template")
        val generatedLicense = file("../config/license/generated/license-$currentYear.txt")
        val detektFile = file("../config/detekt/detekt.yml")
        val projectName = project.findProperty("projectName")?.toString() ?: "stream-feeds-android"

        doLast {
            if (licenseTemplate.exists()) {
                // Generate license
                val licenseContent = licenseTemplate.readText()
                    .replace("{currentYear}", currentYear)
                    .replace("{project}", projectName)
                generatedLicense.writeText(licenseContent)
                println("License file generated: ${generatedLicense.absolutePath}")

                // Update detekt.yml

                if (detektFile.exists()) {
                    val pattern =
                        Regex("""licenseTemplateFile:\s*['"]\.\./license/generated/license-\d{4}\.txt['"]""")
                    val replacement =
                        """licenseTemplateFile: '../license/generated/license-$currentYear.txt'"""
                    val detektContent = detektFile.readText().replace(pattern, replacement)
                    detektFile.writeText(detektContent)

                    println("Detekt configuration updated: ${detektFile.absolutePath}")
                } else {
                    throw FileNotFoundException("Detekt configuration file not found: ${detektFile.absolutePath}")
                }
            } else {
                throw FileNotFoundException("Template file not found: ${licenseTemplate.absolutePath}")
            }
        }
    }
}

private val isSnapshot = System.getenv("SNAPSHOT")?.toBoolean() == true
version = if (isSnapshot) Configuration.snapshotVersionName else Configuration.versionName


subprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure<MavenPublishBaseExtension> {
            publishToMavenCentral(automaticRelease = true)

            configure(
                AndroidSingleVariantLibrary(
                    variant = "release",
                    sourcesJar = true,
                    publishJavadocJar = true,
                )
            )

            pom {
                name.set("stream-feeds-android-client")
                description.set("Stream Feeds official Android SDK")
                url.set("https://github.com/getstream/stream-feeds-android")

                licenses {
                    license {
                        name.set("Stream License")
                        url.set("https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE")
                    }
                }

                developers {
                    developer {
                        id = "aleksandar-apostolov"
                        name = "Aleksandar Apostolov"
                        email = "aleksandar.apostolov@getstream.io"
                    }
                    developer {
                        id = "VelikovPetar"
                        name = "Petar Velikov"
                        email = "petar.velikov@getstream.io"
                    }
                    developer {
                        id = "andremion"
                        name = "André Mion"
                        email = "andre.rego@getstream.io"
                    }
                    developer {
                        id = "rahul-lohra"
                        name = "Rahul Kumar Lohra"
                        email = "rahul.lohra@getstream.io"
                    }
                    developer {
                        id = "gpunto"
                        name = "Gianmarco David"
                        email = "gianmarco.david@getstream.io"
                    }
                }

                scm {
                    connection.set("scm:git:github.com/getstream/stream-feeds-android.git")
                    developerConnection.set("scm:git:ssh://github.com/getstream/stream-feeds-android.git")
                    url.set("https://github.com/getstream/stream-feeds-android/tree/main")
                }
            }
        }
    }
}

tasks.register("printAllArtifacts") {
    group = "publishing"
    description = "Prints all artifacts that will be published"

    doLast {
        subprojects.forEach { subproject ->
            subproject.plugins.withId("com.vanniktech.maven.publish") {
                subproject.extensions.findByType(PublishingExtension::class.java)
                    ?.publications
                    ?.filterIsInstance<MavenPublication>()
                    ?.forEach { println("${it.groupId}:${it.artifactId}:${it.version}") }
            }
        }
    }
}
