pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven {
            url = uri("https://artifactory-external.vkpartner.ru/artifactory/maven")
        }
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven/") }
    }
}

rootProject.name = "Мой Колледж"
include(":app")
include(":baselineprofile")
