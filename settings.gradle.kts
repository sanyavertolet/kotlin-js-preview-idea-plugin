rootProject.name = "kotlin-js-preview"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}


@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


include("plugin")
include("core")
