group = "com.sanyavertolet.kotlinjspreview"
description = "IDEA plugin for Kotlin/JS React FC preview"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

plugins {
    alias(libs.plugins.dokka)
    id("com.sanyavertolet.kotlinjspreview.buildutils.code-quality-convention")
    id("com.sanyavertolet.kotlinjspreview.buildutils.versioning-configuration")
}

