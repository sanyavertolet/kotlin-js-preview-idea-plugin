
package com.sanyavertolet.kotlinjspreview.buildutils

import org.gradle.api.Project
import java.io.File

fun readFromFile(filePath: String?) = filePath?.let { File(it) }
    ?.takeIf { it.exists() }
    ?.readLines()
    ?.joinToString("\n")

fun readFromFileOrEnv(
    filePath: String?,
    envName: String,
): String? = readFromFile(filePath) ?: System.getenv(envName)

fun Project.readFromPropertyOrEnv(
    propertyName: String,
    envName: String,
) = readFromFileOrEnv(findProperty(propertyName) as String?, envName)