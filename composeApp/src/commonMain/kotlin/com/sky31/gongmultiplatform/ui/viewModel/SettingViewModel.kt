package com.sky31.gongmultiplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import com.sky31.gongmultiplatform.network.dto.UpdateDto
import com.sky31.gongmultiplatform.network.repository.NotificationRepositoryImpl
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.NetworkResult
import com.sky31.gongmultiplatform.util.PlatformInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class SettingViewModel: ViewModel(), KoinComponent {
    private val notificationRepository: NotificationRepositoryImpl by inject()
    val platformInfo: PlatformInfo by inject()

    private val _updateData = MutableStateFlow<UpdateDto?>(null)
    val updateData = _updateData.asStateFlow()

    private val _topBarTitle = MutableStateFlow("")
    val topBarTitle = _topBarTitle.asStateFlow()

    fun setTopBarTitle(title: String) {
        _topBarTitle.value = title
    }

    suspend fun checkUpdate(): DataState {
        val result = notificationRepository.getUpdateNotification()

        when(result) {
            is NetworkResult.Success -> {
                _updateData.value = result.data
                return DataState.Newest
            }
            is NetworkResult.Error -> {
                return DataState.Error(result.message)
            }
        }
    }
}