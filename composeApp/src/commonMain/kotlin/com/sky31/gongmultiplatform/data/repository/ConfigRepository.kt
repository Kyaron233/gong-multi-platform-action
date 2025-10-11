package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.model.config.GlobalConfig
import com.sky31.gongmultiplatform.ui.theme.ThemeMode

interface ConfigRepository {

    suspend fun insertConfig(config: GlobalConfig)

    suspend fun updateConfig(
        themeMode: ThemeMode? = null
    )

    suspend fun getGlobalConfig(): GlobalConfig?

    suspend fun deleteConfig()
}