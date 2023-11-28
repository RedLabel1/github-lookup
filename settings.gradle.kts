pluginManagement {
    repositories {
        google()
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

rootProject.name = "GitHubLookup"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":data")
include(":repos-ui")
include(":domain")
include(":core")
