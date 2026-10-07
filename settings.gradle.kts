pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven("https://stream-io-repo.com")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://stream-io-repo.com")
    }
}

rootProject.name = "stream-feeds-android"

include(":stream-feeds-android-sample")
include(":stream-feeds-android-client")
include(":stream-feeds-android-network")
include(":metrics:stream-feeds-android-metrics")
