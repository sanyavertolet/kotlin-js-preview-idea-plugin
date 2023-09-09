package com.sanyavertolet.kotlinjspreview

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
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
        add(getControlsPanel(toolWindow), BorderLayout.CENTER)
        val browser = getBrowser(DEFAULT_URL)
        add(browser.component, BorderLayout.CENTER)
    }

    @Suppress("SameParameterValue")
    private fun getBrowser(url: String) = JBCefBrowser(url).apply {
        val myDevTools: CefBrowser = cefBrowser.devTools
        JBCefBrowser.createBuilder()
            .setCefBrowser(myDevTools)
            .setClient(jbCefClient)
            .build()
    }

    private fun getControlsPanel(toolWindow: ToolWindow) = JPanel().apply {
        val hideButton = JButton("Hide")
        hideButton.addActionListener { _: ActionEvent? -> toolWindow.hide(null) }
        add(hideButton)
    }
    companion object {
        private const val DEFAULT_PORT = 8080
        const val DEFAULT_URL = "https://localhost:$DEFAULT_PORT"
        const val ID = "KotlinJsPreview"
    }
}
