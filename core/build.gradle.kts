import com.sanyavertolet.kotlinjspreview.buildutils.configureSigning

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    id("com.sanyavertolet.kotlinjspreview.buildutils.publishing-configuration")
    id("com.sanyavertolet.kotlinjspreview.buildutils.code-quality-convention")
    signing
    `maven-publish`
}

group = "com.sanyavertolet.kotlinjspreview"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        moduleName = "jsPreview"
        browser()
    }

    sourceSets {
        val jsMain by getting
    }
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn(tasks.withType<Sign>())
}

configureSigning()
