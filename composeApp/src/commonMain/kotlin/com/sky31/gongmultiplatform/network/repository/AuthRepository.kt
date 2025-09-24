package com.sky31.gongmultiplatform.network.repository

import com.sky31.gongmultiplatform.network.response.LoginResponse

interface AuthRepository {

    suspend fun login(username: String, password: String): LoginResponse
}