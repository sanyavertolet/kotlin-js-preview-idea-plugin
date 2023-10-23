rootProject.name = "kotlin-js-preview-idea-plugin"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.9.10" apply false
}

includeBuild("gradle/plugins")
include("plugin")
include("core")
