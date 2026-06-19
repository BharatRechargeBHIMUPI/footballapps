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
        maven { url = uri("https://jitpack.io") }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

    }
}

rootProject.name = "Football prediction 1"
include(":harsh_app_personal")
include(":app4")
include(":app5")
include(":app6")
include(":app7")
include(":app8")
include(":app9")
include(":app10")
include(":app11")
include(":app12-apppcreator")
include(":nsibaba_personal_app")
include(":app")
include(":main")
