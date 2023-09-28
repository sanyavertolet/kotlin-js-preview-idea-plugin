import com.sanyavertolet.kotlinjspreview.buildutils.configureSigning
import com.sanyavertolet.kotlinjspreview.buildutils.readFromPropertyOrEnv

plugins {
    alias(libs.plugins.intellij)
    alias(libs.plugins.kotlin.jvm)
    id("java")
    id("com.sanyavertolet.kotlinjspreview.buildutils.code-quality-convention")
}

group = "com.sanyavertolet"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java", "Kotlin"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {

        sinceBuild.set("223")
        untilBuild.set("232.*")
        version.set(project.version.toString())
    }

    val jetbrainsMarketplaceToken = findProperty("jb.token") as String? ?: System.getenv("JB_TOKEN")

    signPlugin {
        certificateChain.set(readFromPropertyOrEnv("jb.chain", "CERTIFICATE_CHAIN"))
        privateKey.set(readFromPropertyOrEnv("jb.private", "PRIVATE_KEY"))
        password.set(readFromPropertyOrEnv("jb.password", "PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(jetbrainsMarketplaceToken)
    }
}

configureSigning()
