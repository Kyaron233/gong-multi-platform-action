package com.sky31.gongmultiplatform.ui.component.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sky31.gongmultiplatform.di.LocalNavController
import com.sky31.gongmultiplatform.ui.viewModel.AuthViewModel
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.baseline_arrow_back_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawerBottomBar(
    state: DrawerState
) {
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = viewModel { AuthViewModel() }
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 15.dp, top = 5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    scope.launch {
                        authViewModel.logout()
                        state.close()
                        navController.navigate("login")
                    }
                }
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 8.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "退出登录",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(
            modifier = Modifier
                .width(10.dp)
        )

        IconButton(
            onClick = {
                scope.launch {
                    state.close()
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_arrow_back_24),
                    contentDescription = "back",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }
    }
}