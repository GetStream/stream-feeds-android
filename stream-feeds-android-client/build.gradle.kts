import io.getstream.feeds.android.Configuration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kover)
}

apply(from = "$rootDir/scripts/android.gradle")

android {
    namespace = "io.getstream.feeds.android.client"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "PRODUCT_NAME", "\"stream-feeds-android\"")
        buildConfigField("String", "PRODUCT_VERSION", "\"${Configuration.versionName}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            consumerProguardFiles("consumer-rules.pro")
        }
        debug {
            consumerProguardFiles("consumer-rules.pro")
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-progressive",
                "-Xconsistent-data-class-copy-visibility",
                "-Xexplicit-api=strict",
                "-opt-in=io.getstream.kotlin.base.annotation.marker.StreamInternalApi",
            ),
        )
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    // Stream
    api(project(":stream-android-core"))
    api(project(":stream-feeds-android-network"))
    implementation(project(":stream-annotations"))
    implementation(libs.stream.log)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.process)

    // Networking
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit.scalars)
    ksp(libs.moshi.codegen)

    // Detekt
    detektPlugins(libs.detekt.formatting)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
