plugins {
    alias(libs.plugins.stream.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.getstream.feeds.android.metrics"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("debug")
        }
    }

    flavorDimensions += "sdk"

    productFlavors {
        create("stream-feeds-android-client-baseline") {
            dimension = "sdk"
        }
        create("stream-feeds-android-client-stream") {
            dimension = "sdk"
        }
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.appcompat)

    "stream-feeds-android-client-streamImplementation"(project(":stream-feeds-android-client"))
}
