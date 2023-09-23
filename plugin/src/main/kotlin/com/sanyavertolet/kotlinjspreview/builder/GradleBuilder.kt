package com.sanyavertolet.kotlinjspreview.builder

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.utils.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.getPathOrException
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
        ProgressExecutionMode.IN_BACKGROUND_ASYNC,
        false,
    )

    private fun getBuildSettings(project: Project) = ExternalSystemTaskExecutionSettings()
        .apply {
            val tempProjectPath = project.getPathOrException { NO_PROJECT_DIR }
                .let { "${it.path}/$BUILD_DIR/${config.tempProjectDirName}" }
            externalProjectPath = tempProjectPath
            taskNames = Collections.singletonList(BUILD_COMMAND_TASK_NAME)
            externalSystemIdString = GRADLE_SYSTEM_ID.id
            vmOptions = ""
        }

    companion object {
        private const val BUILD_COMMAND_TASK_NAME = "jsBrowserDistribution"
    }
}
