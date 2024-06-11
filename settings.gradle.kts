@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
    versionCatalogs {
        arrayOf("libs", "release")
            .forEach {
                create(it) {
                    from(files("gradle/toml/${it}.versions.toml"))
                }
            }
    }
}

rootProject.name = "gson-nulldefense"
