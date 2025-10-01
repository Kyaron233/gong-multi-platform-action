package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.data.local.dao.ConfigDao
import com.sky31.gongmultiplatform.data.local.domain.ConfigEntity
import com.sky31.gongmultiplatform.data.local.source.ConfigEntitySourceImpl
import com.sky31.gongmultiplatform.model.GlobalConfigData
import com.sky31.gongmultiplatform.ui.theme.ThemeMode
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

    override suspend fun updateConfig(
        themeMode: ThemeMode?
    ) {
        val oldEntity = source.getConfigEntity()

        if(oldEntity == null) {
            throw Exception("cannot update config because there is no configEntity.")
        }

        if(oldEntity.globalConfig == null) {
            throw Exception("cannot update config because there is no globalConfig in configEntity.")
        }

        val globalConfig = GlobalConfigData(
            themeMode = themeMode ?: Json.decodeFromString<GlobalConfigData>(oldEntity.globalConfig).themeMode
        )

        val entity = ConfigEntity(
            globalConfig = Json.encodeToString<GlobalConfigData>(globalConfig)
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