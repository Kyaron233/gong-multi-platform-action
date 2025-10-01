package com.sky31.gongmultiplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.sky31.gongmultiplatform.data.repository.AcademicDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.CourseDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.ExamDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.PublicDataRepositoryImpl
import com.sky31.gongmultiplatform.model.bearerTokenStorage
import com.sky31.gongmultiplatform.network.HttpClientProvider
import com.sky31.gongmultiplatform.network.repository.AuthRepositoryImpl
import com.sky31.gongmultiplatform.util.AuthState
import com.sky31.gongmultiplatform.util.NetworkResult
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthViewModel: ViewModel(), KoinComponent {
    private val publicDataRepository: PublicDataRepositoryImpl by inject()
    private val academicDataRepository: AcademicDataRepositoryImpl by inject()
    private val courseDataRepository: CourseDataRepositoryImpl by inject()
    private val examDataRepository: ExamDataRepositoryImpl by inject()

    private val authRepository: AuthRepositoryImpl by inject()
    private val settings: Settings by inject()

    private val _authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
    val authState = _authState.asStateFlow()

    init {
        loadAuthState()
    }

    private fun loadAuthState() {
        val token = settings.get<String>("token")

        println("load from local: $token")

        if(token !== null) {
            bearerTokenStorage.add(BearerTokens(token, ""))
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Unauthenticated
    }

    suspend fun login(username: String, password: String) {
        _authState.value = AuthState.Loading

        when(val result = authRepository.login(username, password)) {
            is NetworkResult.Success -> {
                bearerTokenStorage.add(BearerTokens(result.data.accessToken, ""))
                settings.putString("token", result.data.accessToken)
                _authState.value = AuthState.Authenticated

                // 触发 loadTokens
                HttpClientProvider.client.authProvider<BearerAuthProvider>()?.clearToken()
            }

            is NetworkResult.Error -> {
                _authState.value = AuthState.Error(result.message)
            }
        }
    }

    suspend fun logout() {
        publicDataRepository.deleteAllPublicData()
        academicDataRepository.deleteAllAcademicData()
        courseDataRepository.deleteAllCourses()
        examDataRepository.deleteAllExams()

        bearerTokenStorage.clear()
        HttpClientProvider.client.authProvider<BearerAuthProvider>()?.clearToken()

        settings.remove("token")
        _authState.value = AuthState.Unauthenticated
    }
}