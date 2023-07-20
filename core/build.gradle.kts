import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "1.9.0"
}

group = "com.sanyavertolet"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        moduleName = "jsPreview"
        browser {
            binaries.executable()
            @Suppress("DEPRECATION")
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
            webpackTask(Action<KotlinWebpack> {
                mainOutputFileName.set("js-preview.js")
            })
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.598"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom")
            }
        }
    }
}
