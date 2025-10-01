package com.sky31.gongmultiplatform.ui.screen.mainScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky31.gongmultiplatform.di.LocalNavController
import com.sky31.gongmultiplatform.ui.component.CircleProgressBar
import com.sky31.gongmultiplatform.ui.component.CustomScrollBox
import com.sky31.gongmultiplatform.ui.component.LoadingRing
import com.sky31.gongmultiplatform.ui.viewModel.MainViewModel
import com.sky31.gongmultiplatform.util.AnimationState
import com.sky31.gongmultiplatform.util.CourseState
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.getCourseState
import com.sky31.gongmultiplatform.util.safeApiCallsSequential
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.course
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun TodayCourseBox(
    viewModel: MainViewModel
) {
    val navController = LocalNavController.current

    val scope = rememberCoroutineScope()

    // 已完成课程进度
    val progression by viewModel.progression.collectAsState()

    val completedCourseNum by viewModel.completedCourseNum.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val courseList by viewModel.courseList.collectAsState()

    var courseListState by remember { mutableStateOf<DataState>(DataState.Uninitialized) }
    val refreshState by remember {
        derivedStateOf {
            when (courseListState) {
                is DataState.Loading,
                is DataState.Expired -> AnimationState.Loading
                is DataState.Uninitialized -> AnimationState.Unstarted
                else -> AnimationState.Finished
            }
        }
    }

    val blurValue = remember { Animatable(0f) }

    val update: suspend () -> Unit = {
        courseListState = DataState.Loading

        val results = safeApiCallsSequential(
            calls = listOf(
                { viewModel.updateCalendar() },
                { viewModel.updateCourseList() }
            )
        )

        courseListState = results[1]
    }

    // 获取课表数据
    LaunchedEffect(Unit) {
        update()
    }

    LaunchedEffect(courseListState) {
        when(courseListState) {
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

    // 更新进度条
    LaunchedEffect(courseList, currentTime) {
        if (courseListState == DataState.Loading || courseListState == DataState.Uninitialized) {
            return@LaunchedEffect
        }
        if (courseList.isEmpty()) {
            viewModel.setProgression(1f)
            return@LaunchedEffect
        }

        var accomplishment = 0f
        courseList.forEach {
            if (getCourseState(
                    currentTime,
                    it.startTime,
                    it.duration
                ) is CourseState.Before
            )
                accomplishment += 1f
        }
        viewModel.setProgression(accomplishment / courseList.size)
        viewModel.setCompletedNum(accomplishment.toInt())
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
                text = "今日课程",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)
            )

            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .size(42.dp)
            ) {
                CircleProgressBar(
                    target = progression,
                    courseCount = courseList.size
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "共",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = "${courseList.size}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight(800),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                    )

                    Text(
                        text = "节课",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "已上",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = "$completedCourseNum",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight(800),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp)
                    )

                    Text(
                        text = "节课",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            IconButton(
                onClick = {
                    scope.launch {
                        update()
                    }
                }
            ) {
                LoadingRing(
                    state = refreshState
                )
            }

            IconButton(
                onClick = {
                    navController.navigate("courseScreen")
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.course),
                    contentDescription = "course",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }

        // 课程容器
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
            if (courseList.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "今日无课",
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
                                painter = painterResource(Res.drawable.course),
                                contentDescription = "course",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(20.dp)
                            )

                            Text(
                                text = "课程表",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            } else {
                CustomScrollBox(
                    Modifier
                        .fillMaxSize(), Alignment.CenterHorizontally
                ) {
                    courseList.forEach { it ->
                        CourseBox(it, currentTime)
                    }
                }
            }
        }
    }
}