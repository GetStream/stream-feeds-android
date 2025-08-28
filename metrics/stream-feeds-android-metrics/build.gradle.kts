plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

apply(from = "$rootDir/scripts/android.gradle")

android {
    namespace = "io.getstream.feeds.android.metrics"
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
