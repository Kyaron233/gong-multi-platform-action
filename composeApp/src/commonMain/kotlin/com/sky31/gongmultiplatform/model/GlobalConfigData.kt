package com.sky31.gongmultiplatform.model

import com.sky31.gongmultiplatform.ui.theme.ThemeMode
import kotlinx.serialization.Serializable

@Serializable
data class GlobalConfigData(
    val themeMode: ThemeMode? = ThemeMode.SYSTEM
)
