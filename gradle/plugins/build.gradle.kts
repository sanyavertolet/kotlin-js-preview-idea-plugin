import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

run {
    dependencies {
        implementation(libs.reckon.gradle.plugin)
        implementation(libs.detekt.gradle.plugin) {
            exclude("io.github.detekt.sarif4k", "sarif4k")
        }
        implementation(libs.diktat.gradle.plugin) {
            exclude("io.github.detekt.sarif4k", "sarif4k")
        }
        implementation(libs.sarif4k)
        implementation(libs.publish.gradle.plugin)
        implementation(libs.gradle.plugin.spotless)

        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.gradle.plugin)
        implementation(libs.kotlin.stdlib.common)
        implementation(libs.kotlin.stdlib.jdk8)
    }
}
