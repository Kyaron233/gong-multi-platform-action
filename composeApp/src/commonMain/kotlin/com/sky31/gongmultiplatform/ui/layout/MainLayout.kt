package com.sky31.gongmultiplatform.ui.layout

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sky31.gongmultiplatform.ui.component.AuthorizationDialog
import com.sky31.gongmultiplatform.ui.component.drawer.MainScreenDrawer
import com.sky31.gongmultiplatform.ui.screen.academicScreen.AcademicScreen
import com.sky31.gongmultiplatform.ui.screen.classroomScreen.ClassroomScreen
import com.sky31.gongmultiplatform.ui.screen.mainScreen.MainScreen


@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerState = drawerState,
        drawerContent = {
            MainScreenDrawer(drawerState)
        }
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    navController = navController
                )
            }
        ) { innerPadding ->
            AuthorizationDialog()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(innerPadding),
            ) {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut(
                            targetAlpha = 0f,
                            animationSpec = tween(
                                durationMillis = 5
                            )
                        )
                    },
                ) {
                    composable("home") {
                        MainScreen(drawerState)
                    }

                    composable("emptyClassroom") {
                        ClassroomScreen()
                    }

                    composable("academic") {
                        AcademicScreen()
                    }
                }
            }
        }
    }
}