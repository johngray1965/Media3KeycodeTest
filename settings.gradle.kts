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

rootProject.name = "Media3KeyCodeTest"
include(":app")

// To enable a local version of media3
//(gradle as ExtensionAware).extra["androidxMediaModulePrefix"] = "media3-"
//apply(from = file("../media/core_settings.gradle"))

