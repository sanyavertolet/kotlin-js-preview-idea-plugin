package com.sanyavertolet.kotlinjspreview.copier

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.sanyavertolet.kotlinjspreview.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.createChildDirectoryIfNotCreated
import com.sanyavertolet.kotlinjspreview.orException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.io.File
import java.nio.file.NoSuchFileException

class RecursiveProjectCopier: ProjectCopier {
    private val config: PluginConfig = PluginConfig.getInstance()

    override fun copy(project: Project) {
        val tempDir = runWriteAction { createTempDir(project) }
        val projectDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        runWriteAction {
            copyFilesRecursively(projectDir, tempDir)
        }
    }

    private fun copyFilesRecursively(source: VirtualFile, dist: VirtualFile) {
        val sourceFile = File(source.path)
        val distFile = File(dist.path)
        val dirFilter = FileFilterUtils.notFileFilter(
            FileFilterUtils.or(
                *config.copyIgnoreFileNames.map { FileFilterUtils.nameFileFilter(it) }.toTypedArray()
            )
        )
        runWriteAction { FileUtils.copyDirectory(sourceFile, distFile, dirFilter) }
    }

    private fun createTempDir(project: Project): VirtualFile {
        val projectBaseDir = project.guessProjectDir().orException { NO_PROJECT_DIR }

        val tempDirPath = "${projectBaseDir.path}/$BUILD_DIR/${config.tempProjectDirName}"
        val tempDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(tempDirPath)

        return tempDir?.let { file ->
            if (file.exists()) {
                tempDir
            } else {
                projectBaseDir.createChildDirectoryIfNotCreated(BUILD_DIR)
                    .createChildDirectoryIfNotCreated(config.tempProjectDirName)
            }
        } ?: throw NoSuchFileException("Could not create temp directory with path [${tempDirPath}]")
    }
}
