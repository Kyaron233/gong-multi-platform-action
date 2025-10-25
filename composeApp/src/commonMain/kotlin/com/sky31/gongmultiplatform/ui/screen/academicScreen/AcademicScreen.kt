package com.sky31.gongmultiplatform.ui.screen.academicScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sky31.gongmultiplatform.ui.component.AuthorizationDialog
import com.sky31.gongmultiplatform.ui.viewModel.AcademicViewModel
import com.sky31.gongmultiplatform.util.DataState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicScreen() {
    val scope = rememberCoroutineScope()
    val viewModel: AcademicViewModel = viewModel { AcademicViewModel() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )

    val majorAcademicInfoState by viewModel.majorAcademicInfoState.collectAsState()
    val minorAcademicInfoState by viewModel.minorAcademicInfoState.collectAsState()
    val compulsoryRankState by viewModel.compulsoryRankState.collectAsState()
    val totalRankState by viewModel.totalRankState.collectAsState()


    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        refreshing = true
        viewModel.update()
        refreshing = false
    }

    DisposableEffect(Unit) {
        if(majorAcademicInfoState is DataState.Uninitialized
            || minorAcademicInfoState is DataState.Uninitialized
            || compulsoryRankState is DataState.Uninitialized
            || totalRankState is DataState.Uninitialized) {
            scope.launch {
                refreshing = true
                viewModel.update()
                refreshing = false
            }
        }

        onDispose {
            viewModel.resetLoadingState()
        }
    }

    AuthorizationDialog()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    )

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AcademicBottomBar(pagerState)

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

