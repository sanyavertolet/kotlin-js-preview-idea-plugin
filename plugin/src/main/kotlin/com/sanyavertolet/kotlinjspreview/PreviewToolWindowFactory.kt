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
        val content = ContentFactory.getInstance()
            .createContent(getPanelContent(project, toolWindow), "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun getPanelContent(project: Project, toolWindow: ToolWindow): JPanel = JPanel().apply {
        add(getControlsPanel(toolWindow), BorderLayout.CENTER)
        val browser = getBrowser(getPathToHtml(project))
        add(browser.component, BorderLayout.CENTER)
        browser.loadURL(getPathToHtml(project))
    }

    @Suppress("SameParameterValue")
    private fun getBrowser(uri: String) = JBCefBrowser(uri).apply {
        val myDevTools: CefBrowser = cefBrowser.devTools
        JBCefBrowser.createBuilder()
            .setCefBrowser(myDevTools)
            .setClient(jbCefClient)
            .build()
    }

    fun getPathToHtml(project: Project) = project.getPathOrException { NO_PROJECT_DIR }
        .let { file ->
            "file://${file.path}/$BUILD_DIR/$PROJECT_TEMP_DIR_NAME/$BUILD_DIR/dist/js/productionExecutable/index.html"
        }

    private fun getControlsPanel(toolWindow: ToolWindow) = JPanel().apply {
        val hideButton = JButton("Hide")
        hideButton.addActionListener { _: ActionEvent? -> toolWindow.hide(null) }
        add(hideButton)
    }
    companion object {
        const val ID = "KotlinJsPreview"
    }
}
