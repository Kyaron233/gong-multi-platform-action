package com.sky31.gongmultiplatform.network.api

import com.sky31.gongmultiplatform.network.request.LoginRequest
import com.sky31.gongmultiplatform.network.response.LoginResponse

interface AuthApi {
    suspend fun login(request: LoginRequest): LoginResponse
}