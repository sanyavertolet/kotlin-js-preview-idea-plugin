package com.sanyavertolet.kotlinjspreview.builder

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.sanyavertolet.kotlinjspreview.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.getPathOrException
import com.sanyavertolet.kotlinjspreview.task.JsPreviewNotifierCallback
import org.jetbrains.kotlin.idea.configuration.GRADLE_SYSTEM_ID
import org.jetbrains.kotlin.idea.util.application.runWriteAction
import java.util.*

class GradleBuilder : Builder {
    private val config: PluginConfig = PluginConfig.getInstance()

    override fun build(project: Project) = runWriteAction { runBuildTaskForTempProject(project) }

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
            val tempProjectPath = project.getPathOrException { NO_PROJECT_DIR }
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