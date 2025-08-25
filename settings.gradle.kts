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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "stream-feeds-android"

// Always resolve from Maven first, but if we have the local repo, override it
if (file("stream-android-core").exists()) {
    includeBuild("stream-android-core") {
        dependencySubstitution {
            substitute(module("io.getstream.android:stream-android-core:0.0.1"))
                .using(project(":stream-android-core"))
            substitute(module("io.getstream.android:stream-android-annotations:0.0.1"))
                .using(project(":stream-android-core-annotations"))
        }
    }
}


include(":stream-feeds-android-sample")
include(":stream-feeds-android-client")
include(":stream-feeds-android-network")
