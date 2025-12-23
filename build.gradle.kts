// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.stream.project)
    alias(libs.plugins.stream.android.library) apply false
    alias(libs.plugins.stream.android.application) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.arturbosch.detekt) apply true
    id("com.google.gms.google-services") version "4.4.3" apply false
}

streamProject {
    spotless {
        useKtfmt = true
    }

    coverage {
        includedModules = setOf("stream-feeds-android-client")
    }

    publishing {
        description = "Stream Feeds official Android SDK"
    }
}

detekt {
    autoCorrect = true
    toolVersion = "1.23.8"
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
