package com.sanyavertolet.kotlinjspreview.copier

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.utils.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.createChildDirectoryIfNotCreated
import com.sanyavertolet.kotlinjspreview.utils.orException
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFilterUtils
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.io.File

class SimpleProjectCopier: ProjectCopier {
    private val config: PluginConfig = PluginConfig.getInstance()

    override fun copy(project: Project) {
        val projectDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        val tempDir = runWriteAction { createTempDir(project) }
        runWriteAction { copyFilesRecursively(projectDir, tempDir) }
    }

    private fun copyFilesRecursively(source: VirtualFile, dist: VirtualFile) {
        val dirFilter = FileFilterUtils.notFileFilter(
            FileFilterUtils.or(
                *config.copyIgnoreFileNames.map { FileFilterUtils.nameFileFilter(it) }.toTypedArray()
            )
        )
        FileUtils.copyDirectory(File(source.path), File(dist.path), dirFilter)
    }

    private fun createTempDir(project: Project): VirtualFile {
        val projectBaseDir = project.guessProjectDir().orException { NO_PROJECT_DIR }

        val tempDirPath = "${projectBaseDir.path}/$BUILD_DIR/${config.tempProjectDirName}"
        val tempDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(tempDirPath)

        return tempDir?.takeIf { it.exists() }
            ?: projectBaseDir.createChildDirectoryIfNotCreated(BUILD_DIR)
                .createChildDirectoryIfNotCreated(config.tempProjectDirName)
    }
}
