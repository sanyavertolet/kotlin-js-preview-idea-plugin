package com.sanyavertolet.kotlinjspreview.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*


class PluginConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private var tempProjectDirNameField: JTextField? = null
    private var copyIgnoreFileNamesListModel: DefaultListModel<String>? = null
    private var copyIgnoreFileNamesList: JList<String>? = null
    private var fileNameInputField: JTextField? = null
    private var fileNameAddButton: JButton? = null
    private var fileNameRemoveButton: JButton? = null

    override fun createComponent(): JComponent? {
        settingsPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.PAGE_AXIS) }
        tempProjectDirNameField = JTextField(PluginConfig.getInstance().tempProjectDirName).apply {
            size = Dimension(preferredSize.width, 1)
        }

        copyIgnoreFileNamesList = JBList(*PluginConfig.getInstance().copyIgnoreFileNames.toTypedArray())

        copyIgnoreFileNamesListModel = DefaultListModel<String>().apply {
            addAll(PluginConfig.getInstance().copyIgnoreFileNames)
        }

        fileNameInputField = JTextField(20).apply {
            addActionListener { _ ->
                fileNameRemoveButton?.isEnabled = copyIgnoreFileNamesList?.isSelectionEmpty == false
                fileNameAddButton?.isEnabled = fileNameInputField?.text?.isNotEmpty() == true
            }
        }

        fileNameAddButton = JButton("Add").apply {
            addActionListener { _ ->
                val textToAdd = fileNameInputField?.text
                if (textToAdd?.isEmpty() == false && copyIgnoreFileNamesListModel?.contains(textToAdd) == false) {
                    copyIgnoreFileNamesListModel?.addElement(textToAdd)
                    fileNameInputField?.text = ""
                }
            }
        }

        fileNameRemoveButton = JButton("Remove").apply {
            addActionListener { _ ->
                val selectedIndex: Int = copyIgnoreFileNamesList?.selectedIndex ?: -1
                if (selectedIndex != -1) {
                    copyIgnoreFileNamesList?.remove(selectedIndex)
                    copyIgnoreFileNamesListModel?.remove(selectedIndex)
                }
            }
        }

        val ignoreFileNamesPanel = JPanel()
        ignoreFileNamesPanel.setLayout(BoxLayout(ignoreFileNamesPanel, BoxLayout.PAGE_AXIS))

        val inputPanel = JPanel(FlowLayout()).apply {
            add(fileNameInputField)
            add(fileNameAddButton)
            add(fileNameRemoveButton)
        }

        val listScrollPane = JBScrollPane(copyIgnoreFileNamesList)

        ignoreFileNamesPanel.apply {
            add(inputPanel)
            add(listScrollPane)
        }

        val tempProjectDirNameLabeledPanel = JPanel(FlowLayout()).apply {
            add(JLabel("Temp Project Directory Name:"))
            add(tempProjectDirNameField)
        }

        val ignoreFileNamesLabeledPanel = JPanel(FlowLayout()).apply {
            add(JLabel("Copy Ignore File Names:"))
            add(ignoreFileNamesPanel)
        }

        settingsPanel?.apply {
            add(tempProjectDirNameLabeledPanel)
            add(ignoreFileNamesLabeledPanel)
        }

        return settingsPanel
    }

    override fun isModified(): Boolean {
        val settings = PluginConfig.getInstance()
        val isTempProjectDirNameChanged = tempProjectDirNameField?.text != settings.tempProjectDirName
        val isIgnoreFileNamesListChanged = settings.copyIgnoreFileNames.toSet() != copyIgnoreFileNamesListModel?.elements()?.toList()?.toSet()
        return isIgnoreFileNamesListChanged || isTempProjectDirNameChanged
    }

    override fun apply() {
        val settings = PluginConfig.getInstance()
        settings.tempProjectDirName = tempProjectDirNameField?.text ?: PluginConfig.DEFAULT_TEMP_PROJECT_DIR_NAME
        settings.copyIgnoreFileNames = copyIgnoreFileNamesListModel?.elements()?.toList() ?: PluginConfig.defaultCopyIgnoreFileNames
    }

    override fun reset() {
        val settings = PluginConfig.getInstance()
        tempProjectDirNameField?.text = settings.tempProjectDirName
        copyIgnoreFileNamesListModel?.apply {
            clear()
            addAll(settings.copyIgnoreFileNames)
        }
    }

    override fun getDisplayName(): String {
        return "Kotlin JS Preview Settings"
    }
}