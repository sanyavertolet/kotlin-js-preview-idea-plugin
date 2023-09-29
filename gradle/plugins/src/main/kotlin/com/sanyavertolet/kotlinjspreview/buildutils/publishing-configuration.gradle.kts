package com.sanyavertolet.kotlinjspreview.buildutils

import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing

plugins {
    `maven-publish`
    signing
}

run {
    System.getenv("GPG_SEC")?.let { extra.set("signingKey", it) }
    System.getenv("GPG_PASSWORD")?.let { extra.set("signingPassword", it) }
    configureGitHubPublishing()
    configurePublications()
}
