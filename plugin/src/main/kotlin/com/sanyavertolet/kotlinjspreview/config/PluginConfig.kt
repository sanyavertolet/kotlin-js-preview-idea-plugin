package com.sanyavertolet.kotlinjspreview.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Plugin configuration data class
 *
 * @property tempProjectDirName name of a dir inside of `build` directory, where a copy of project is located
 * @property copyIgnoreFileNames file/dir names that should not be copied to temp dir
 */
@Service(Service.Level.PROJECT)
@State(
    name = "KotlinJsPreviewPluginConfig",
    storages = [Storage("kotlinJsPreview.xml")],
)
data class PluginConfig(
    var tempProjectDirName: String = DEFAULT_TEMP_PROJECT_DIR_NAME,
    var copyIgnoreFileNames: List<String> = defaultCopyIgnoreFileNames,
) : PersistentStateComponent<PluginConfig> {
    override fun getState(): PluginConfig {
        return this
    }

    override fun loadState(state: PluginConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): PluginConfig = service<PluginConfig>()

        /**
         * Default value of [tempProjectDirName]
         */
        const val DEFAULT_TEMP_PROJECT_DIR_NAME = "jsPreviewTemp"

        /**
         * Default value of [copyIgnoreFileNames]
         */
        val defaultCopyIgnoreFileNames = listOf(
            ".idea",
            ".gradle",
            ".run",
            "build",
            ".gitignore",
        )
    }
}
