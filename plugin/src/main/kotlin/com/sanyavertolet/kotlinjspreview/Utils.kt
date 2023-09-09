package com.sanyavertolet.kotlinjspreview

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.createChildDirectoryIfNotCreated(dirName: String): VirtualFile {
    val file = LocalFileSystem.getInstance().refreshAndFindFileByPath("$path/$dirName")
    return file?.takeIf { it.exists() } ?: createChildDirectory(this, dirName)
}

fun VirtualFile?.orException(messageBuilder: () -> String = { "" }) = this ?: throw IllegalStateException(messageBuilder())
