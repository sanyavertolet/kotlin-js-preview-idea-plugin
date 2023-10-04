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

/**
 * [ToolWindowFactory] that builds a tool window with JS preview
 */
class PreviewToolWindowFactory : ToolWindowFactory {
    private val config: PluginConfig = PluginConfig.getInstance()
    private val contentFactory = ContentFactory.getInstance()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = contentFactory.createContent(getPanelContent(project), "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun getPanelContent(project: Project): JPanel = JPanel(BorderLayout()).apply {
        val browser = getBrowser(getPathToHtml(project))
        add(getControlsPanel(browser), BorderLayout.CENTER)
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

    private fun getControlsPanel(cefBrowser: JBCefBrowser) = JPanel().apply {
        val hideButton = JButton("Reload").apply {
            addActionListener { cefBrowser.cefBrowser.reloadIgnoreCache() }
        }
        val zoomOutButton = JButton("-").apply {
            addActionListener { cefBrowser.zoomLevel -= ZOOM_VALUE }
        }
        val zoomInButton = JButton("+").apply {
            addActionListener { cefBrowser.zoomLevel += ZOOM_VALUE }
        }

        add(hideButton)
        add(zoomOutButton)
        add(zoomInButton)
    }

    companion object {
        /**
         * ID of a tool window
         */
        const val ID = "KotlinJsPreview"
        private const val ZOOM_VALUE = 0.5
    }
}
