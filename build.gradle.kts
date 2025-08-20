import java.io.FileNotFoundException
import java.util.Calendar

apply(plugin = "org.jetbrains.dokka")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.arturbosch.detekt) apply true
    alias(libs.plugins.spotless) apply true
    id("com.google.gms.google-services") version "4.4.3" apply false
    alias(libs.plugins.dokka) apply false
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
                    val pattern = Regex("""licenseTemplateFile:\s*['"]\.\./license/generated/license-\d{4}\.txt['"]""")
                    val replacement = """licenseTemplateFile: '../license/generated/license-$currentYear.txt'"""
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
