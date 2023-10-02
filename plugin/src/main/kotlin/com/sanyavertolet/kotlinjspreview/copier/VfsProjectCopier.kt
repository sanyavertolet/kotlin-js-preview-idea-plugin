package com.sanyavertolet.kotlinjspreview.copier

import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.utils.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.utils.orException

/**
 * [ProjectCopier] implementation that uses [VfsUtil] under the hood
 */
class VfsProjectCopier : ProjectCopier {
    private val config: PluginConfig = PluginConfig.getInstance()
    private val localFileSystem = LocalFileSystem.getInstance()
    override fun copy(project: Project) {
        val projectDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        val tempDir = createTempDir(project).orException()
        runUndoTransparentWriteAction {
            VfsUtil.copyDirectory(this, projectDir, tempDir) {
                !config.copyIgnoreFileNames.contains(it.name)
            }
        }
    }

    private fun createTempDir(project: Project): VirtualFile? {
        val projectBaseDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        val tempDirPath = "${projectBaseDir.path}/$BUILD_DIR/${config.tempProjectDirName}"
        val tempDir = localFileSystem.refreshAndFindFileByPath(tempDirPath)
        return tempDir?.takeIf { it.exists() } ?: runUndoTransparentWriteAction { VfsUtil.createDirectoryIfMissing(tempDirPath) }
    }
}
