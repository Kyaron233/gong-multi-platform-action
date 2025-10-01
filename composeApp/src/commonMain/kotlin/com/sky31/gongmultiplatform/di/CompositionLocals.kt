package com.sky31.gongmultiplatform.di

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController
import com.sky31.gongmultiplatform.model.GlobalConfigData

val LocalGlobalConfig = staticCompositionLocalOf<GlobalConfigData> {
    error("No GlobalConfig provided! Please wrap your app with GlobalConfigProvider.")
}

val LocalIsDarkTheme = staticCompositionLocalOf<Boolean> {
    error("No IsDarkTheme provided! Please wrap your app with IsDarkThemeProvider.")
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No NavController provided")
}