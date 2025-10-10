package com.sky31.gongmultiplatform.ui.screen.academicScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sky31.gongmultiplatform.ui.component.AuthorizationDialog
import com.sky31.gongmultiplatform.ui.viewModel.AcademicViewModel
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.baseline_arrow_back_ios_new_24
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val viewModel: AcademicViewModel = viewModel { AcademicViewModel() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        refreshing = true
        viewModel.update()
        refreshing = false
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                            .clickable {
                                navController.navigate("main")
                            },
                        painter = painterResource(Res.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "back_arrow",
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "学业成绩",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

            }
        },
        bottomBar = {
            AcademicBottomBar(pagerState)
        }
    ) { innerPadding ->

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
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    if (page == 0) {
                        MainInfoSubScreen(viewModel)
                    } else {
                        ScoreSubScreen(viewModel)
                    }
                }
            }
        }

    }
}

