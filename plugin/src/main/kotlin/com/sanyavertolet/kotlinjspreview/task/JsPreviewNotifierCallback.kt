package com.sanyavertolet.kotlinjspreview.task // ktlint-disable filename

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.externalSystem.task.TaskCallback
import com.intellij.openapi.project.Project

class JsPreviewNotifierCallback(private val project: Project?) : TaskCallback {
    override fun onSuccess() {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification("Built preview.", NotificationType.INFORMATION)
            .notify(project)
    }

    override fun onFailure() {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification("Could not run JsPreview.", NotificationType.ERROR)
            .notify(project)
    }

    companion object {
        private const val NOTIFICATION_GROUP_ID = "js-preview-notification"
    }
}
