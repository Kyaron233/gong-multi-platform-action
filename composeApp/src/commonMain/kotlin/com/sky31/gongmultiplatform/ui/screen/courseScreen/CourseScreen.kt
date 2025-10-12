package com.sky31.gongmultiplatform.ui.screen.courseScreen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sky31.gongmultiplatform.ui.component.AuthorizationDialog
import com.sky31.gongmultiplatform.ui.viewModel.CourseViewModel
import com.sky31.gongmultiplatform.util.CustomTime
import com.sky31.gongmultiplatform.util.customTimeToString
import com.sky31.gongmultiplatform.util.getStartTime
import com.sky31.gongmultiplatform.util.reverseWeekdayNameMap
import com.sky31.gongmultiplatform.util.weekdayNameMapCN
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * 课程表页面
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen() {
    val scope = rememberCoroutineScope()
    val viewModel: CourseViewModel = viewModel { CourseViewModel() }

    val currentWeekNum by viewModel.currentWeekNum.collectAsState()

    val calendar by viewModel.calendar.collectAsState()
    var refreshing by remember { mutableStateOf(false) }

    // HorizontalPager的状态
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { calendar?.weeks ?: 0 }
    )

    LaunchedEffect(currentWeekNum) {
        pagerState.scrollToPage(currentWeekNum.toInt() - 1)
    }

    AuthorizationDialog()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    )

    Scaffold(
        modifier = Modifier
            .safeDrawingPadding(),
        topBar = {
            CourseTopBar(
                state = pagerState
            )
        }
    ) { innerPadding ->
        CourseBottomSheet()

        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                scope.launch {
                    refreshing = true
                    viewModel.update()
                    refreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                                var weekStart = LocalDate.parse(calendar!!.start)
                                weekStart = weekStart.plus(DatePeriod(days = page * 7))
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
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(top = 2.dp, bottom = 2.dp),
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
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.labelSmall
                                        )

                                        Text(
                                            text = (date.month.ordinal + 1).toString().padStart(2, '0')
                                                    + "-"
                                                    + date.day.toString().padStart(2, '0'),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}