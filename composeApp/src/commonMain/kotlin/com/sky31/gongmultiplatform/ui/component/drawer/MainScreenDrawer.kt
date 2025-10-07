package com.sky31.gongmultiplatform.ui.component.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sky31.gongmultiplatform.network.dto.UpdateDto
import com.sky31.gongmultiplatform.network.repository.NotificationRepositoryImpl
import com.sky31.gongmultiplatform.ui.component.LoadingRing
import com.sky31.gongmultiplatform.ui.component.rememberDialogState
import com.sky31.gongmultiplatform.util.AppUpdateState
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.NetworkResult
import com.sky31.gongmultiplatform.util.PlatformInfo
import com.sky31.gongmultiplatform.util.Toast
import com.sky31.gongmultiplatform.util.getAppUpdateState
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

/**
 * mainScreen左侧栏
 *
 * @param state DrawerState
 */
@Composable
fun MainScreenDrawer(
    state: DrawerState
) {
    val scope = rememberCoroutineScope()
    val platformInfo: PlatformInfo = getKoin().get()
    val notificationRepository: NotificationRepositoryImpl = getKoin().get()

    var updateData by remember { mutableStateOf<UpdateDto?>(null) }
    val appUpdateState by remember {
        derivedStateOf {
            updateData?.let { getAppUpdateState(platformInfo.getVersionName(), it.lastVersion, it.leastVersion) }
        }
    }

    val dialogState = rememberDialogState()

    var loadingRingVisible by remember { mutableStateOf(false) }

    suspend fun checkUpdate(): DataState {
        val result = notificationRepository.getUpdateNotification()

        when(result) {
            is NetworkResult.Success -> {
                updateData = result.data
                return DataState.Newest
            }
            is NetworkResult.Error -> {
                println(result.toString())
                return DataState.Error(result.message)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth(.65f)
            .clip(RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp))
            .background(MaterialTheme.colorScheme.background),
        bottomBar = {
            DrawerBottomBar(state)
        }
    ) { innerPadding ->
        val top = innerPadding.calculateTopPadding()
        val bottom = innerPadding.calculateBottomPadding()

        Column(
            modifier = Modifier
                .padding(top = top, bottom = bottom, start = 8.dp, end = 8.dp)
        ) {
            appUpdateState?.let {
                UpdateNotificationDialog(
                    state = dialogState,
                    appUpdateState = it,
                    data = updateData!!
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 8.dp, bottom = 8.dp),
            ) {
                DrawerMenuItem(
                    name = "用户条款"
                )

                DrawerMenuItem(
                    name = "检查更新",
                    click = {
                        scope.launch {
                            loadingRingVisible = true
                            val result = checkUpdate()
                            loadingRingVisible = false

                            when(result) {
                                is DataState.Newest ->
                                    when(appUpdateState) {
                                        AppUpdateState.UP_TO_DATE -> Toast.show("当前为最新版本")
                                        AppUpdateState.OPTIONAL_UPDATE -> dialogState.show()
                                        else -> {}
                                    }
                                else -> {}
                            }
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(
                            visible = loadingRingVisible,
                            modifier = Modifier
                                .padding(end = 5.dp)
                        ) {
                            LoadingRing(size = 12.dp)
                        }

                        Text(
                            text = platformInfo.getVersionName(),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
        }
    }
}