package com.sanyavertolet.kotlinjspreview.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile?.orException(messageBuilder: () -> String = { "" }) = this ?: throw IllegalStateException(messageBuilder())

fun Project.getPathOrException(
    exceptionMessageBuilder: () -> String = { "" }
) = guessProjectDir().orException(exceptionMessageBuilder)

const val NO_PROJECT_DIR = "Could not find project dir"

const val BUILD_DIR = "build"
