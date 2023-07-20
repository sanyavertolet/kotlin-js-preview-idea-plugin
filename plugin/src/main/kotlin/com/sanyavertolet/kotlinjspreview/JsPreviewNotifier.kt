package com.sanyavertolet.kotlinjspreview // ktlint-disable filename

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object JsPreviewNotifier {
    fun notifyError(project: Project?, content: String?) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("js-preview-notification")
            .createNotification(content.orEmpty(), NotificationType.ERROR)
            .notify(project)
    }

    fun notifySuccess(project: Project?, content: String?) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("js-preview-notification")
            .createNotification(content.orEmpty(), NotificationType.INFORMATION)
            .notify(project)
    }
}
