package com.sanyavertolet.kotlinjspreview.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.JBColor
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import java.awt.FlowLayout
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JComponent
import javax.swing.Box
import javax.swing.JButton
import javax.swing.ListSelectionModel
import javax.swing.JLabel
import javax.swing.UIManager

class PluginConfigurable : Configurable, Configurable.Beta {
    private var settingsPanel: JPanel? = null
    private var tempProjectDirNameField: JTextField? = null
    private var copyIgnoreFileNamesListModel: DefaultListModel<String>? = null
    private var copyIgnoreFileNamesList: JList<String>? = null
    private var fileNameInputField: JTextField? = null

    @Suppress("MagicNumber")
    override fun createComponent(): JComponent? {
        settingsPanel = JPanel(GridBagLayout())

        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.weightx = 1.0
        gbc.anchor = GridBagConstraints.NORTHWEST

        val wipDisclaimerTextArea = getDisclaimerSection()
        gbc.gridy = 0
        settingsPanel?.add(wipDisclaimerTextArea, gbc)

        val generalSettingsSection = getGeneralSettingsSection()
        gbc.gridy = 1
        gbc.insets = JBUI.insetsTop(INSETS_TOP)
        settingsPanel?.add(generalSettingsSection, gbc)

        val ignoreFileNamesPanel = getCopyIgnoreFileNamesListSection()
        gbc.gridy = 2
        gbc.insets = JBUI.insetsTop(INSETS_TOP)
        settingsPanel?.add(ignoreFileNamesPanel, gbc)

        gbc.gridy = 3
        gbc.weighty = 1.0
        settingsPanel?.add(Box.createVerticalBox(), gbc)

        return settingsPanel
    }

    private fun getCopyIgnoreFileNamesListSection(): JComponent {
        val fileNameAddButton = JButton("Add").apply {
            toolTipText = "Add the specified name to the ignore list."
            addActionListener { _ ->
                fileNameInputField?.text
                    ?.takeIf { it.isNotBlank() }
                    ?.takeIf { copyIgnoreFileNamesListModel?.contains(it) == false }
                    ?.let {
                        copyIgnoreFileNamesListModel?.addElement(it)
                        fileNameInputField?.text = ""
                    }
            }
        }

        val fileNameRemoveButton = JButton("Remove").apply {
            addActionListener { _ ->
                copyIgnoreFileNamesList?.selectedIndex?.let { selectedIndex ->
                    copyIgnoreFileNamesListModel?.remove(selectedIndex)
                }
            }
            toolTipText = "Remove the selected name from the ignore list."
            isEnabled = false
        }

        copyIgnoreFileNamesListModel = DefaultListModel<String>().apply {
            addAll(PluginConfig.getInstance().copyIgnoreFileNames)
        }

        copyIgnoreFileNamesList = JBList(
            *PluginConfig.getInstance().copyIgnoreFileNames.toTypedArray()
        ).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            layoutOrientation = JList.VERTICAL
            visibleRowCount = VISIBLE_ROW_COUNT
            model = copyIgnoreFileNamesListModel
            addListSelectionListener { event ->
                if (!event.valueIsAdjusting) {
                    fileNameRemoveButton.isEnabled = copyIgnoreFileNamesList?.isSelectionEmpty != true
                }
            }
        }

        fileNameInputField = JTextField(COLUMNS).apply {
            toolTipText = "Enter the name of the file or directory to ignore during copy."
            addActionListener { fileNameAddButton.isEnabled = text.isNotBlank() }
        }

        val inputPanel = JPanel(FlowLayout()).apply {
            add(fileNameInputField)
            add(fileNameAddButton)
            add(fileNameRemoveButton)
        }

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.empty(TOP_AND_BOTTOM_BORDERS, LEFT_AND_RIGHT_BORDERS)

            val gbc = GridBagConstraints()
            gbc.fill = GridBagConstraints.HORIZONTAL
            gbc.weightx = 1.0
            gbc.gridx = 0

            gbc.gridy = 0
            add(TitledSeparator("Copy Ignore List"), gbc)

            gbc.gridy = 1
            gbc.weighty = 0.0
            add(inputPanel, gbc)

            gbc.gridy = 2
            gbc.weighty = 1.0
            add(JBScrollPane(copyIgnoreFileNamesList), gbc)
        }
    }

    private fun getGeneralSettingsSection(): JComponent {
        tempProjectDirNameField = JBTextField(PluginConfig.getInstance().tempProjectDirName, COLUMNS).apply {
            toolTipText = "Name of the temporary directory to be used by the plugin."
        }

        val tempProjectDirNameLabeledField = JPanel(FlowLayout(FlowLayout.LEADING)).apply {
            border = JBUI.Borders.empty(TOP_AND_BOTTOM_BORDERS, LEFT_AND_RIGHT_BORDERS)
            add(JLabel("Temp Project Directory Name:"))
            add(tempProjectDirNameField)
        }

        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.empty(TOP_AND_BOTTOM_BORDERS, LEFT_AND_RIGHT_BORDERS)

            val gbc = GridBagConstraints()
            gbc.fill = GridBagConstraints.HORIZONTAL
            gbc.weightx = 1.0
            gbc.gridx = 0

            gbc.gridy = 0
            add(TitledSeparator("General Settings", tempProjectDirNameLabeledField), gbc)

            gbc.gridy = 1
            gbc.weighty = 0.0
            add(tempProjectDirNameLabeledField, gbc)
        }
    }

    private fun getDisclaimerSection(): JComponent = JBTextArea(1, 2 * COLUMNS).apply {
        text = "Note: Settings are still being developed. Functionality might not work as expected."
        wrapStyleWord = true
        lineWrap = true
        isEditable = false
        isFocusable = false
        foreground = JBColor.GRAY
        background = UIManager.getColor("Label.background")
        font = UIManager.getFont("Label.font")
        border = JBUI.Borders.empty()
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
    companion object {
        private const val TOP_AND_BOTTOM_BORDERS = 5
        private const val LEFT_AND_RIGHT_BORDERS = 0
        private const val INSETS_TOP = 10
        private const val COLUMNS = 20
        private const val VISIBLE_ROW_COUNT = 7
    }
}
