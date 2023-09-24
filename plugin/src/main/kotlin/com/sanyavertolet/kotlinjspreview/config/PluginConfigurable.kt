package com.sanyavertolet.kotlinjspreview.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*


class PluginConfigurable : Configurable, Configurable.Beta {
    private var settingsPanel: JPanel? = null
    private var tempProjectDirNameField: JTextField? = null
    private var copyIgnoreFileNamesListModel: DefaultListModel<String>? = null
    private var copyIgnoreFileNamesList: JList<String>? = null
    private var fileNameInputField: JTextField? = null
    private var fileNameAddButton: JButton? = null
    private var fileNameRemoveButton: JButton? = null

    override fun createComponent(): JComponent? {
        settingsPanel = JPanel().apply {
            layout = GridLayout(3, 1)
        }

        val margin = JBUI.insets(5)

        val wipDisclaimerTextArea = getDisclaimerSection()
        val tempProjectDirNameLabeledPanel = getTempProjectDirNameLabeledPanel(margin)
        val ignoreFileNamesPanel = getCopyIgnoreFileNamesListSection(margin)

        return settingsPanel?.apply {
            add(wipDisclaimerTextArea, BorderLayout.PAGE_START)
            add(tempProjectDirNameLabeledPanel, BorderLayout.CENTER)
            add(ignoreFileNamesPanel, BorderLayout.CENTER)
        }
    }

    private fun getTempProjectDirNameLabeledPanel(margin: JBInsets): JComponent {
        tempProjectDirNameField = JTextField(PluginConfig.getInstance().tempProjectDirName, 20).apply {
            toolTipText = "Name of the temporary directory to be used by the plugin."
        }

        return JPanel(FlowLayout()).apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("General settings"),
                JBUI.Borders.empty(margin.top, margin.left, margin.bottom, margin.right)
            )
            add(JLabel("Temp Project Directory Name:"))
            add(tempProjectDirNameField)
        }
    }

    private fun getDisclaimerSection(): JComponent = JBTextArea(2, 40).apply {
        text = "Note: Settings are still being developed. Functionality might not work as expected."
        wrapStyleWord = true
        lineWrap = true
        isEditable = false
        isFocusable = false
        background = UIManager.getColor("Label.background")
        font = UIManager.getFont("Label.font")
        border = JBUI.Borders.empty()
    }

    private fun getCopyIgnoreFileNamesListSection(margin: JBInsets): JComponent {
        fileNameInputField = JTextField(20).apply {
            addActionListener { _ ->
                fileNameRemoveButton?.isEnabled = copyIgnoreFileNamesList?.isSelectionEmpty == false
                fileNameAddButton?.isEnabled = fileNameInputField?.text?.isNotEmpty() == true
            }
            toolTipText = "Enter the name of the file or directory to ignore during copy."
        }

        fileNameAddButton = JButton("Add").apply {
            addActionListener { _ ->
                val textToAdd = fileNameInputField?.text
                if (textToAdd?.isEmpty() == false && copyIgnoreFileNamesListModel?.contains(textToAdd) == false) {
                    copyIgnoreFileNamesListModel?.addElement(textToAdd)
                    fileNameInputField?.text = ""
                }
            }
            toolTipText = "Add the specified name to the ignore list."
        }

        fileNameRemoveButton = JButton("Remove").apply {
            addActionListener { _ ->
                val selectedIndex: Int = copyIgnoreFileNamesList?.selectedIndex ?: -1
                if (selectedIndex != -1) {
                    copyIgnoreFileNamesList?.remove(selectedIndex)
                    copyIgnoreFileNamesListModel?.remove(selectedIndex)
                }
            }
            toolTipText = "Remove the selected name from the ignore list."
        }

        copyIgnoreFileNamesListModel = DefaultListModel<String>().apply {
            addAll(PluginConfig.getInstance().copyIgnoreFileNames)
        }

        copyIgnoreFileNamesList = JBList(*PluginConfig.getInstance().copyIgnoreFileNames.toTypedArray())

        val inputPanel = JPanel(FlowLayout()).apply {
            add(fileNameInputField)
            add(fileNameAddButton)
            add(fileNameRemoveButton)
        }

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
            border = JBUI.Borders.compound(
                BorderFactory.createTitledBorder("Copy Ignore List"),
                JBUI.Borders.empty(margin.top, margin.left, margin.bottom, margin.right)
            )
            add(inputPanel)
            add(
                JBScrollPane(copyIgnoreFileNamesList)
            )
        }
    }

    override fun isModified(): Boolean = PluginConfig.getInstance().let { settings ->
        val isTempProjectDirNameChanged = tempProjectDirNameField?.text != settings.tempProjectDirName
        val isIgnoreFileNamesListChanged = settings.copyIgnoreFileNames.toSet() != copyIgnoreFileNamesListModel?.elements()?.toList()?.toSet()
        isIgnoreFileNamesListChanged || isTempProjectDirNameChanged
    }

    override fun apply() {
        PluginConfig.getInstance().apply {
            tempProjectDirName = tempProjectDirNameField?.text ?: PluginConfig.DEFAULT_TEMP_PROJECT_DIR_NAME
            copyIgnoreFileNames = copyIgnoreFileNamesListModel?.elements()?.toList() ?: PluginConfig.defaultCopyIgnoreFileNames
        }
    }

    override fun reset() {
        PluginConfig.getInstance().apply {
            tempProjectDirNameField?.text = tempProjectDirName
            copyIgnoreFileNamesListModel?.apply {
                clear()
                addAll(copyIgnoreFileNames)
            }
        }
    }

    override fun getDisplayName(): String = "Kotlin JS Preview"
}