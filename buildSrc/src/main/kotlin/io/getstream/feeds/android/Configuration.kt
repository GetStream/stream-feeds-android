package io.getstream.feeds.android

object Configuration {
    const val compileSdk = 36
    const val targetSdk = 36
    const val sampleTargetSdk = 36
    const val minSdk = 21
    const val majorVersion = 3
    const val minorVersion = 0
    const val patchVersion = 0
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "io.getstream"
}
