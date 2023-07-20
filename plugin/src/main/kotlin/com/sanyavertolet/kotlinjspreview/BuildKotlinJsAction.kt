package com.sanyavertolet.kotlinjspreview

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.task.TaskCallback
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import org.jetbrains.kotlin.idea.configuration.GRADLE_SYSTEM_ID
import java.util.*

class BuildKotlinJsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = getEventProject(event) ?: return
        val settings = ExternalSystemTaskExecutionSettings()
            .apply {
                externalProjectPath = project.basePath
                taskNames = Collections.singletonList("jsRun")
                externalSystemIdString = GRADLE_SYSTEM_ID.id
                vmOptions = ""
            }

        val callback = object : TaskCallback {
            override fun onSuccess() {
                JsPreviewNotifier.notifySuccess(project, "Done.")
            }

            override fun onFailure() {
                JsPreviewNotifier.notifyError(project, "Could not run JsPreview.")
            }
        }

        ExternalSystemUtil.runTask(
            settings,
            DefaultRunExecutor.EXECUTOR_ID,
            project,
            GRADLE_SYSTEM_ID,
            callback,
            ProgressExecutionMode.NO_PROGRESS_ASYNC,
            true,
        )
    }
}
