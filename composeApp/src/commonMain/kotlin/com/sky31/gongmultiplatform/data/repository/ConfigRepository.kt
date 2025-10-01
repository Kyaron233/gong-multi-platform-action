package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.model.GlobalConfigData
import com.sky31.gongmultiplatform.ui.theme.ThemeMode

interface ConfigRepository {

    suspend fun insertConfig(config: GlobalConfigData)

    suspend fun updateConfig(
        themeMode: ThemeMode? = null
    )

    suspend fun getGlobalConfig(): GlobalConfigData?

    suspend fun deleteConfig()
}