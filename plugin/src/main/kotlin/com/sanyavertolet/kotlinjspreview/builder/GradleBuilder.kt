package com.sanyavertolet.kotlinjspreview.builder

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.sanyavertolet.kotlinjspreview.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.orException
import com.sanyavertolet.kotlinjspreview.task.JsPreviewNotifierCallback
import org.jetbrains.kotlin.idea.configuration.GRADLE_SYSTEM_ID
import java.util.*

class GradleBuilder : Builder {
    private val config: PluginConfig = PluginConfig.getInstance()

    override fun build(project: Project) = runBuildTaskForTempProject(project)

    private fun runBuildTaskForTempProject(project: Project) = ExternalSystemUtil.runTask(
        getBuildSettings(project),
        DefaultRunExecutor.EXECUTOR_ID,
        project,
        GRADLE_SYSTEM_ID,
        JsPreviewNotifierCallback(project),
        ProgressExecutionMode.NO_PROGRESS_ASYNC,
        true,
    )

    private fun getBuildSettings(project: Project) = ExternalSystemTaskExecutionSettings()
        .apply {
            val tempProjectPath = project.guessProjectDir()
                .orException { NO_PROJECT_DIR }
                .let { "${it.path}/$BUILD_DIR/${config.tempProjectDirName}" }
            externalProjectPath = tempProjectPath
            taskNames = Collections.singletonList(BUILD_COMMAND)
            externalSystemIdString = GRADLE_SYSTEM_ID.id
            vmOptions = ""
        }

    companion object {
        private const val BUILD_COMMAND = "jsBrowserDistribution"
    }
}