package com.sanyavertolet.kotlinjspreview.config

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.PROJECT)
@State(
    name = "KotlinJsPreviewPluginConfig",
    storages = [Storage("kotlinJsPreview.xml")],
)
data class PluginConfig(
    var tempProjectDirName: String = DEFAULT_TEMP_PROJECT_DIR_NAME,
    var copyIgnoreFileNames: List<String> = defaultCopyIgnoreFileNames
): PersistentStateComponent<PluginConfig> {
    override fun getState(): PluginConfig {
        return this
    }

    override fun loadState(state: PluginConfig) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): PluginConfig {
            return service<PluginConfig>()
        }

        const val DEFAULT_TEMP_PROJECT_DIR_NAME = "jsPreviewTemp"
        val defaultCopyIgnoreFileNames = listOf(
            ".idea",
            ".gradle",
            ".run",
            "build",
            ".gitignore",
        )
    }
}
