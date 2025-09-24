package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.data.local.dao.ConfigDao
import com.sky31.gongmultiplatform.data.local.domain.ConfigEntity
import com.sky31.gongmultiplatform.data.local.source.ConfigEntitySourceImpl
import com.sky31.gongmultiplatform.model.GlobalConfigData
import kotlinx.serialization.json.Json

class ConfigRepositoryImpl(
    dao: ConfigDao
): ConfigRepository {

    private val source = ConfigEntitySourceImpl(dao)

    override suspend fun insertConfig(config: GlobalConfigData) {
        source.insertConfigEntity(ConfigEntity(
            globalConfig = Json.encodeToString<GlobalConfigData>(config)
        ))
    }

    override suspend fun updateConfig(globalConfig: GlobalConfigData?) {
        val oldEntity = source.getConfigEntity()

        val entity = ConfigEntity(
            globalConfig = globalConfig?.let { Json.encodeToString<GlobalConfigData>(globalConfig) } ?: oldEntity?.globalConfig
        )

        source.updateConfigEntity(entity)
    }

    override suspend fun getGlobalConfig(): GlobalConfigData? {
        return source.getGlobalConfig()?.let { Json.decodeFromString<GlobalConfigData>(it) }
    }

    override suspend fun deleteConfig() {
        source.deleteAllConfigs()
    }
}