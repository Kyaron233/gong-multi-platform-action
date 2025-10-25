package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.data.local.dao.ConfigDao
import com.sky31.gongmultiplatform.data.local.domain.ConfigEntity
import com.sky31.gongmultiplatform.data.local.source.ConfigEntitySourceImpl
import com.sky31.gongmultiplatform.model.config.AuthConfig
import com.sky31.gongmultiplatform.model.config.FunctionalConfig
import com.sky31.gongmultiplatform.model.config.GlobalConfig
import com.sky31.gongmultiplatform.model.config.NotificationConfig
import com.sky31.gongmultiplatform.ui.theme.ThemeMode
import kotlinx.serialization.json.Json

class ConfigRepositoryImpl(
    dao: ConfigDao
): ConfigRepository {

    private val source = ConfigEntitySourceImpl(dao)

    override suspend fun insertConfig(globalConfig: GlobalConfig, functionalConfig: FunctionalConfig) {
        source.insertConfigEntity(ConfigEntity(
            globalConfig = Json.encodeToString<GlobalConfig>(globalConfig),
            functionalConfig = Json.encodeToString<FunctionalConfig>(functionalConfig)
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

        val globalConfig = GlobalConfig(
            themeMode = themeMode ?: Json.decodeFromString<GlobalConfig>(oldEntity.globalConfig).themeMode
        )

        val entity = ConfigEntity(
            globalConfig = Json.encodeToString<GlobalConfig>(globalConfig)
        )

        source.updateConfigEntity(entity)
    }

    override suspend fun updateFunctionalConfig(config: FunctionalConfig) {
        source.updateFunctionalConfig(Json.encodeToString(config))
    }

    override suspend fun getGlobalConfig(): GlobalConfig? {
        return source.getGlobalConfig()?.let { Json.decodeFromString<GlobalConfig>(it) }
    }

    override suspend fun getFunctionalConfig(): FunctionalConfig? {
        return source.getFunctionalConfig()?.let { Json.decodeFromString<FunctionalConfig>(it) }
    }

    override suspend fun getAuthConfig(): AuthConfig? {
        return getFunctionalConfig()?.authConfig
    }

    override suspend fun getNotificationConfig(): NotificationConfig? {
        return getFunctionalConfig()?.notificationConfig
    }

    override suspend fun deleteConfig() {
        source.deleteAllConfigs()
    }
}