package com.sky31.gongmultiplatform.ui.viewModel

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.sky31.gongmultiplatform.data.repository.AcademicDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.CourseDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.ExamDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.PublicDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.UserInfoDataRepositoryImpl
import com.sky31.gongmultiplatform.model.bearerTokenStorage
import com.sky31.gongmultiplatform.network.HttpClientProvider
import com.sky31.gongmultiplatform.network.repository.AuthRepositoryImpl
import com.sky31.gongmultiplatform.util.AuthState
import com.sky31.gongmultiplatform.util.NetworkResult
import com.sky31.gongmultiplatform.util.authMsgMap
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
    private val userInfoDataRepository: UserInfoDataRepositoryImpl by inject()

    private val authRepository: AuthRepositoryImpl by inject()
    private val settings: Settings by inject()

    private val _authState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
    val authState = _authState.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username = _username.asStateFlow()

    init {
        loadAuthState()

        _username.value = settings.get<String>("username")
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

    suspend fun login(username: String, password: String): NetworkResult<Unit> {
        _authState.value = AuthState.Loading

        return when(val result = authRepository.login(username, password)) {
            is NetworkResult.Success -> {
                bearerTokenStorage.add(BearerTokens(result.data.accessToken, ""))
                settings.putString("token", result.data.accessToken)

                settings.putString("username", username)
                _username.value = username

                _authState.value = AuthState.Authenticated

                // 触发 loadTokens
                HttpClientProvider.client.authProvider<BearerAuthProvider>()?.clearToken()

                NetworkResult.Success(data = Unit)
            }

            is NetworkResult.Error -> {
                _authState.value = AuthState.Error(result.message)

                NetworkResult.Error(message = result.code?.let { authMsgMap[it] } ?: "未知错误")
            }
        }
    }

    suspend fun logout() {
        publicDataRepository.deleteAllPublicData()
        academicDataRepository.deleteAllAcademicData()
        courseDataRepository.deleteAllCourses()
        examDataRepository.deleteAllExams()
        userInfoDataRepository.deleteAllUserInfo()

        bearerTokenStorage.clear()
        HttpClientProvider.client.authProvider<BearerAuthProvider>()?.clearToken()

        settings.remove("token")
        settings.remove("username")
        _authState.value = AuthState.Unauthenticated
    }
}