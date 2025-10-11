package com.sky31.gongmultiplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import com.sky31.gongmultiplatform.data.repository.ConfigRepositoryImpl
import com.sky31.gongmultiplatform.model.config.FunctionalConfig
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfigViewModel: ViewModel(), KoinComponent {
    val configRepository: ConfigRepositoryImpl by inject()

    val reauthentication = MutableStateFlow(true)

    suspend fun loadConfig() {
        val result = configRepository.getFunctionalConfig()

        if(result === null) {
            configRepository.insertConfig()
        } else {
            reauthentication.value = result.reauthentication
        }
    }

    suspend fun updateFunctionalConfig() {
        val config = FunctionalConfig(
            reauthentication = reauthentication.value
        )

        println("update result is $config")

        configRepository.updateFunctionalConfig(config)
    }
}