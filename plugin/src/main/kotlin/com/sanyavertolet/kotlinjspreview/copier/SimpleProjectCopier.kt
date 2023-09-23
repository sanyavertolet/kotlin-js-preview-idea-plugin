package com.sanyavertolet.kotlinjspreview.copier

import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.utils.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.utils.createChildDirectoryIfNotCreated
import com.sanyavertolet.kotlinjspreview.utils.orException
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class SimpleProjectCopier: ProjectCopier {
    private val config: PluginConfig = PluginConfig.getInstance()

    override fun copy(project: Project) {
        val projectDir = project.guessProjectDir().orException { NO_PROJECT_DIR }
        val tempDir = createTempDir(project)
        runWriteAction { copyProjectRecursively(projectDir.path, tempDir.path) }
    }

    private fun copyProjectRecursively(sourceDir: String, destDir: String) {
        val srcDir = File(sourceDir)
        val dstDir = File(destDir)
        if (srcDir.isDirectory()) {
            if (!dstDir.exists()) {
                dstDir.mkdir()
            }
            val children = srcDir.list()
            if (children != null) {
                for (child in children) {
                    if (!config.copyIgnoreFileNames.contains(child)) {
                        copyProjectRecursively(File(srcDir, child).path, File(dstDir, child).path)
                    }
                }
            }
        } else {
            val sourcePath: Path = Paths.get(srcDir.absolutePath)
            val destPath: Path = Paths.get(dstDir.absolutePath)
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun createTempDir(project: Project): VirtualFile {
        val projectBaseDir = project.guessProjectDir().orException { NO_PROJECT_DIR }

        val tempDirPath = "${projectBaseDir.path}/$BUILD_DIR/${config.tempProjectDirName}"
        val tempDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(tempDirPath)

        return tempDir?.takeIf { it.exists() }
            ?: runUndoTransparentWriteAction {
                projectBaseDir.createChildDirectoryIfNotCreated(BUILD_DIR)
                    .createChildDirectoryIfNotCreated(config.tempProjectDirName)
            }
    }
}
