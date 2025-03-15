pluginManagement {
    repositories {
        google() // Google's Maven repository
        mavenCentral() // Central repository for dependencies
        gradlePluginPortal() // Repository for Gradle plugins
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Enforce resolution from declared repositories
    repositories {
        google() // Google's Maven repository for Android dependencies
        mavenCentral() // Central repository for Java and Kotlin dependencies
    }
}

// Root project configuration
rootProject.name = "HeavyPath_Project"

// Include application module
include(":app")
