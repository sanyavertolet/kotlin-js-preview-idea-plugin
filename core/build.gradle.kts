import com.sanyavertolet.kotlinjspreview.buildutils.configureSigning

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.sanyavertolet.kotlinjspreview.buildutils.publishing-configuration")
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

configureSigning()
