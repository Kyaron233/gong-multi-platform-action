package com.sky31.gongmultiplatform.ui.screen.courseScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sky31.gongmultiplatform.ui.viewModel.CourseViewModel
import com.sky31.gongmultiplatform.util.CustomTime
import com.sky31.gongmultiplatform.util.DataState
import com.sky31.gongmultiplatform.util.customTimeToString
import com.sky31.gongmultiplatform.util.getStartTime
import com.sky31.gongmultiplatform.util.reverseWeekdayNameMap
import com.sky31.gongmultiplatform.util.weekdayNameMapCN
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.baseline_arrow_back_ios_new_24
import gongmultiplatform.composeapp.generated.resources.error
import gongmultiplatform.composeapp.generated.resources.expired
import gongmultiplatform.composeapp.generated.resources.newest
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.painterResource

/**
 * 课程表页面
 *
 * @param navController 导航控制器
 */
@Composable
fun CourseScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val viewModel: CourseViewModel = viewModel { CourseViewModel() }

    val currentWeekNum by viewModel.currentWeekNum.collectAsState()

    val calendar by viewModel.calendar.collectAsState()

    // 课程表数据状态
    val courseMapState by viewModel.courseMapState.collectAsState()

    // 周次选择列表状态
    var weekListState by remember { mutableStateOf(false) }

    // 数据状态icon id
    val stateIconResource by remember {
        derivedStateOf {
            when (courseMapState) {
                is DataState.Uninitialized -> Res.drawable.expired
                is DataState.Newest -> Res.drawable.newest
                is DataState.Expired -> Res.drawable.expired
                is DataState.Loading -> Res.drawable.expired
                is DataState.Error,
                is DataState.Unauthorized -> Res.drawable.error
            }
        }
    }

    // HorizontalPager的状态
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { calendar?.weeks ?: 0 }
    )

    LaunchedEffect(currentWeekNum) {
        pagerState.scrollToPage(currentWeekNum.toInt() - 1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        )
    }

    Scaffold(
        modifier = Modifier
            .safeDrawingPadding(),
        topBar = {
            // 用于解决text和周次选择列表无法居中的问题
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(start = 15.dp, end = 15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // 切换animate
                    AnimatedContent(
                        targetState = weekListState,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith
                                    fadeOut(tween(300)) using SizeTransform(clip = false)
                        },
                        label = "animatedWeekList",
                    ) { targetState ->
                        if (targetState) {
                            if (calendar != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp)
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Spacer(modifier = Modifier.width(25.dp))
                                    for (index in 1..calendar!!.weeks)
                                        Text(
                                            modifier = Modifier
                                                .padding(start = 10.dp, end = 10.dp)
                                                .clickable {
                                                    scope.launch { pagerState.scrollToPage(index - 1) }
                                                    weekListState = false
                                                },
                                            text = "$index",
                                            color = Color.White
                                        )

                                    Spacer(modifier = Modifier.width(25.dp))
                                }
                            }
                        } else {
                            Text(
                                modifier = Modifier
                                    .clickable { weekListState = true },
                                text = "第${pagerState.currentPage + 1}周",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0.0f to colorScheme.primary,
                                    0.15f to Color.Transparent,
                                    0.70f to Color.Transparent,
                                    0.85f to colorScheme.primary,
                                )
                            ),
                            size = size
                        )
                    }
                }
                // icon row 回退、日历下载、状态的层次
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .padding(5.dp)
                            .width(20.dp)
                            .height(20.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { navController.navigate("main") },
                        painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "left_arrow"
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                            .clickable {
                                navController.navigate("main")
                            },
                        painter = painterResource(stateIconResource),
                        contentDescription = "state"
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            modifier = Modifier
                .padding(innerPadding),
            state = pagerState,
        ) { page ->
            val courseMap by viewModel.getWeekCourseMap(page.toLong() + 1).collectAsState(initial = null)

            if (calendar !== null && courseMap !== null) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    // 左侧时间表
                    Column(
                        modifier = Modifier
                            .width(42.dp)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val weekStart = LocalDate.parse(calendar!!.start)
                        weekStart.plus(DatePeriod(days = page * 7))
                        val startTime = getStartTime(weekStart)

                        // 月份box
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${weekStart.month.ordinal + 1}",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        startTime.forEachIndexed { index, start ->
                            val end = CustomTime(
                                hour = start.hour + (start.minute + 45) / 60,
                                minute = (start.minute + 45) % 60
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(top = 1.dp, bottom = 1.dp)
                                    .background(MaterialTheme.colorScheme.surface),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = customTimeToString(start),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = customTimeToString(end),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            // 中午和晚上的间隔
                            if (index == 3 || index == 7) {
                                Spacer(
                                    modifier = Modifier
                                        .height(5.dp)
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }

                    // 遍历周一至周日
                    courseMap!!.keys.forEachIndexed { index, item ->
                        CourseColumn(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            courseList = courseMap!![item]?.sortedBy { course -> course.startTime }
                                ?: listOf(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .height(40.dp)
                                    .fillMaxWidth()
                                    .padding(start = 1.dp, end = 1.dp)
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val date = LocalDate.parse(calendar!!.start).plus(DatePeriod(days = page * 7 + index))

                                Text(
                                    text = weekdayNameMapCN.getValue(
                                        reverseWeekdayNameMap.getValue(
                                            item
                                        )
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = (date.month.ordinal + 1).toString().padStart(2, '0')
                                            + "-"
                                            + date.day.toString().padStart(2, '0'),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}