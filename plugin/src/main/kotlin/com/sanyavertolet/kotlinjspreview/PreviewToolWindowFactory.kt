package com.sanyavertolet.kotlinjspreview

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

class PreviewToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(getPanelContent(toolWindow), "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun getPanelContent(toolWindow: ToolWindow): JPanel = JPanel().apply {
        add(createControlsPanel(toolWindow), BorderLayout.CENTER)
    }

    private fun createControlsPanel(toolWindow: ToolWindow) = JPanel().apply {
        val hideButton = JButton("Hide")
        hideButton.addActionListener { _: ActionEvent? -> toolWindow.hide(null) }
        add(hideButton)
    }
}
