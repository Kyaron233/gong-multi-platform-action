package com.sky31.gongmultiplatform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sky31.gongmultiplatform.ui.layout.MainLayout
import com.sky31.gongmultiplatform.ui.screen.academicScreen.AcademicScreen
import com.sky31.gongmultiplatform.ui.screen.configScreen.ConfigScreen
import com.sky31.gongmultiplatform.ui.screen.courseScreen.CourseScreen
import com.sky31.gongmultiplatform.ui.screen.loginScreen.LoginScreen
import com.sky31.gongmultiplatform.ui.viewModel.AuthViewModel
import com.sky31.gongmultiplatform.util.AuthState

@Composable
fun App() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel { AuthViewModel() }

    Box(
        modifier = Modifier
    ) {
        AppProvider(
            navController = navController,
        ) {
            NavHost(
                navController = navController,
                startDestination = if (authViewModel.authState.value is AuthState.Authenticated) "main" else "login"
            ) {
                composable("login") {
                    LoginScreen(navController)
                }
                composable("main") {
                    MainLayout()
                }
                composable("courseScreen") {
                    CourseScreen()
                }
                composable("academicScreen") {
                    AcademicScreen(navController)
                }

                composable("configScreen") {
                    ConfigScreen()
                }
            }
        }
    }
}