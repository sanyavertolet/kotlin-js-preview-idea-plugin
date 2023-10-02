/**
 * File containing different utils
 */
package com.sanyavertolet.kotlinjspreview.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VirtualFile

/**
 * @param messageBuilder message builder
 * @return [this] if it is not null or throws [IllegalStateException] with message from [messageBuilder]
 */
fun VirtualFile?.orException(messageBuilder: () -> String = { "" }) = this ?: error(messageBuilder())

/**
 * @param exceptionMessageBuilder message builder
 * @return guessed project dir or exception with message from [exceptionMessageBuilder]
 */
fun Project.getPathOrException(
    exceptionMessageBuilder: () -> String = { NO_PROJECT_DIR },
) = guessProjectDir().orException(exceptionMessageBuilder)

/**
 * Exception message for no project dir guessed
 */
const val NO_PROJECT_DIR = "Could not find project dir"

/**
 * Build dir name
 */
const val BUILD_DIR = "build"
