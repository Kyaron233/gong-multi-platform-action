package com.sky31.gongmultiplatform.ui.component.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sky31.gongmultiplatform.ui.component.ContinuousScrollText
import com.sky31.gongmultiplatform.ui.component.LoadingRing
import com.sky31.gongmultiplatform.ui.component.rememberDialogState
import com.sky31.gongmultiplatform.ui.viewModel.DrawerViewModel
import com.sky31.gongmultiplatform.util.AppUpdateState
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.Toast
import com.sky31.gongmultiplatform.util.getAppUpdateState
import kotlinx.coroutines.launch

/**
 * mainScreen左侧栏
 *
 * @param state DrawerState
 */
@Composable
fun MainScreenDrawer(
    state: DrawerState
) {
    val viewModel: DrawerViewModel = viewModel { DrawerViewModel() }

    val scope = rememberCoroutineScope()
    val dialogState = rememberDialogState()

    val platformInfo = viewModel.platformInfo
    val updateData by viewModel.updateData.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()

    var loadingRingVisible by remember { mutableStateOf(false) }

    val appUpdateState by remember {
        derivedStateOf {
            updateData?.let { getAppUpdateState(platformInfo.getVersionName(), it.lastVersion, it.leastVersion) }
        }
    }

    LaunchedEffect(state.isOpen) {
        if(state.isOpen) {
            viewModel.update()
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

            DrawerUserInfo(
                modifier = Modifier
                    .fillMaxWidth(),
                userInfo = userInfo
            )

            Text(
                text = "关于",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 8.dp, bottom = 8.dp),
            ) {
                DrawerMenuItem(
                    name = "检查更新",
                    click = {
                        scope.launch {
                            loadingRingVisible = true
                            val result = viewModel.checkUpdate()
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .width(17.dp)
                    ) {
                        AnimatedVisibility(
                            visible = loadingRingVisible,
                            modifier = Modifier
                                .padding(end = 5.dp)
                        ) {
                            LoadingRing(size = 12.dp)
                        }
                    }

                    ContinuousScrollText(
                        text = platformInfo.getVersionName(),
                        modifier = Modifier
                            .fillMaxWidth(0.5f),
                    )
                }

                DrawerMenuItem(
                    name = "权限申请与使用情况说明"
                )

                DrawerMenuItem(
                    name = "隐私政策"
                )

                DrawerMenuItem(
                    name = "用户条款"
                )
            }

            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
        }
    }
}