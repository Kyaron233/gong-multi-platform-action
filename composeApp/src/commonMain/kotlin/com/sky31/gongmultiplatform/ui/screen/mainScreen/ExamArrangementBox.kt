package com.sky31.gongmultiplatform.ui.screen.mainScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sky31.gongmultiplatform.di.LocalAppNavController
import com.sky31.gongmultiplatform.ui.component.CustomScrollBox
import com.sky31.gongmultiplatform.ui.component.DataLoadingRing
import com.sky31.gongmultiplatform.ui.viewModel.MainViewModel
import com.sky31.gongmultiplatform.util.AnimationState
import com.sky31.gongmultiplatform.util.DataState
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.grade
import gongmultiplatform.composeapp.generated.resources.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun ExamArrangementBox(
    viewModel: MainViewModel
) {
    val navController = LocalAppNavController.current
    val scope = rememberCoroutineScope()

    val currentTime by viewModel.currentTime.collectAsState()
    val examList by viewModel.examList.collectAsState()
    val examBoxState by viewModel.examBoxState.collectAsState()
    val refreshState by remember {
        derivedStateOf {
            when (examBoxState) {
                is DataState.Loading -> AnimationState.Loading
                is DataState.Uninitialized -> AnimationState.Unstarted
                else -> AnimationState.Finished
            }
        }
    }

    // 加载时动态模糊效果
    val blurValue = remember { Animatable(0f) }

    DisposableEffect(Unit) {
        if(examBoxState is DataState.Uninitialized) {
            scope.launch {
                viewModel.updateExamBox()
            }
        }

        onDispose {
            if(examBoxState is DataState.Loading) {
                viewModel.resetExamBoxState()
            }
        }
    }

    LaunchedEffect(examBoxState) {
        when(examBoxState) {
            is DataState.Loading -> {
                blurValue.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            else -> {
                blurValue.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // title栏
        Row(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                color = MaterialTheme.colorScheme.onBackground,
                text = "考试安排",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            IconButton(
                onClick = {
                    scope.launch {
                        viewModel.updateExamBox()
                    }
                }
            ) {
                DataLoadingRing(
                    state = refreshState
                )
            }
        }

        // 考试列表容器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(5.dp))
                .blur(blurValue.value.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 5.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (examList.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "暂无考试",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(
                        onClick = {
                            navController.navigate("courseScreen")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,  // 背景透明
                            contentColor = Color.Unspecified
                        ),
                        contentPadding = PaddingValues(start = 25.dp, end = 25.dp),
                        modifier = Modifier.defaultMinSize(0.dp, 0.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.grade),
                                contentDescription = "course",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(20.dp)
                            )

                            Text(
                                text = "成绩单",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            } else {
                CustomScrollBox(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    examList.forEach { it ->
                        ExamBox(it, currentTime)
                    }
                }
            }
        }
    }

    AnimatedContent(
        targetState = examBoxState is DataState.Error
    ) {targetState ->
        if(targetState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // 消耗点击事件，防止下层响应
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                event.changes.forEach { it.consume() } // 👈 消费掉事件，防止下传
                            }
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.update),
                    contentDescription = "loadingRing",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            scope.launch {
                                viewModel.updateExamBox()
                            }
                        }
                )

                Text(
                    text = "重新加载",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}