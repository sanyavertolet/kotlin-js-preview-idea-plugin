package com.sanyavertolet.kotlinjspreview

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.base.utils.fqname.getKotlinFqName
import org.jetbrains.kotlin.psi.KtProperty

fun VirtualFile.createChildDirectoryIfNotCreated(dirName: String): VirtualFile {
    val file = LocalFileSystem.getInstance().refreshAndFindFileByPath("$path/$dirName")
    return file?.takeIf { it.exists() } ?: createChildDirectory(this, dirName)
}

fun VirtualFile?.orException(messageBuilder: () -> String = { "" }) = this ?: throw IllegalStateException(messageBuilder())

fun Project.getPathOrException(
    exceptionMessageBuilder: () -> String = { "" }
) = guessProjectDir().orException(exceptionMessageBuilder)

fun PsiElement.getIdentifier() = (this as KtProperty).getKotlinFqName()

const val NO_PROJECT_DIR = "Could not find project dir"

const val BUILD_DIR = "build"

