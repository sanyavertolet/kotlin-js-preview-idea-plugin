package com.sanyavertolet.kotlinjspreview

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowser
import com.sanyavertolet.kotlinjspreview.config.PluginConfig
import com.sanyavertolet.kotlinjspreview.utils.BUILD_DIR
import com.sanyavertolet.kotlinjspreview.utils.NO_PROJECT_DIR
import com.sanyavertolet.kotlinjspreview.utils.getPathOrException
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel


class PreviewToolWindowFactory : ToolWindowFactory {
    private val config: PluginConfig = PluginConfig.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance()
            .createContent(getPanelContent(project, toolWindow), "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun getPanelContent(project: Project, toolWindow: ToolWindow): JPanel = JPanel(BorderLayout()).apply {
        val browser = getBrowser(getPathToHtml(project))
        add(getControlsPanel(toolWindow, browser), BorderLayout.CENTER)
        add(browser.component, BorderLayout.SOUTH)
        browser.loadURL(getPathToHtml(project))
    }

    @Suppress("SameParameterValue")
    private fun getBrowser(uri: String) = JBCefBrowser(uri).apply {
        JBCefBrowser.createBuilder()
            .setCefBrowser(cefBrowser.devTools)
            .setClient(jbCefClient)
            .setEnableOpenDevToolsMenuItem(true)
            .build()
    }

    private fun getPathToHtml(project: Project) = project.getPathOrException { NO_PROJECT_DIR }
        .let { file ->
            "file://${file.path}/$BUILD_DIR/${config.tempProjectDirName}/$BUILD_DIR/dist/js/productionExecutable/index.html"
        }

    @Suppress("UNUSED_PARAMETER")
    private fun getControlsPanel(toolWindow: ToolWindow, cefBrowser: JBCefBrowser) = JPanel().apply {
        val hideButton = JButton("Reload")
        hideButton.addActionListener { _: ActionEvent? -> cefBrowser.cefBrowser.reloadIgnoreCache() }
        val zoomOutButton = JButton("-").apply {
            addActionListener {
                val currentZoom = cefBrowser.zoomLevel
                cefBrowser.zoomLevel = currentZoom - 0.5 // Adjust this value as necessary
            }
        }
        val zoomInButton = JButton("+").apply {
            addActionListener {
                val currentZoom = cefBrowser.zoomLevel
                cefBrowser.zoomLevel = currentZoom + 0.5
            }
        }
        add(hideButton)
        add(zoomOutButton)
        add(zoomInButton)
    }

    companion object {
        const val ID = "KotlinJsPreview"
    }
}
