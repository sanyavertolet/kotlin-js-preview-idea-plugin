rootProject.name = "kotlin-js-preview-idea-plugin"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.0.0" apply false
}

includeBuild("gradle/plugins")
include("plugin")
include("core")
