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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.sky31.gongmultiplatform.di.LocalNavController
import com.sky31.gongmultiplatform.ui.viewModel.CourseViewModel
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.baseline_arrow_back_ios_new_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun CourseTopBar(
    state: PagerState
) {
    val scope = rememberCoroutineScope()
    val viewModel: CourseViewModel = viewModel { CourseViewModel() }
    val navController = LocalNavController.current
    val colorScheme = MaterialTheme.colorScheme

    val calendar by viewModel.calendar.collectAsState()
    var weekListState by remember { mutableStateOf(false) }

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
                                            scope.launch { state.scrollToPage(index - 1) }
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
                        text = "第${state.currentPage + 1}周",
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
        }
    }
}