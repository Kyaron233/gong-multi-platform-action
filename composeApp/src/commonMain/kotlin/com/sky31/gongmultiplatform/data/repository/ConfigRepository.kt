package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.model.GlobalConfigData

interface ConfigRepository {

    suspend fun insertConfig(config: GlobalConfigData)

    suspend fun updateConfig(
        globalConfig: GlobalConfigData? = null
    )

    suspend fun getGlobalConfig(): GlobalConfigData?

    suspend fun deleteConfig()
}