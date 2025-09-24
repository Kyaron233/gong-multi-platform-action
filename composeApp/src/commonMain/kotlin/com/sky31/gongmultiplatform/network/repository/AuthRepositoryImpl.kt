package com.sky31.gongmultiplatform.network.repository

import com.sky31.gongmultiplatform.network.api.AuthApiImpl
import com.sky31.gongmultiplatform.network.request.LoginRequest
import com.sky31.gongmultiplatform.network.response.LoginResponse
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    client: HttpClient
): AuthRepository {
    private val api = AuthApiImpl(client)

    override suspend fun login(username: String, password: String): LoginResponse {
        val request = LoginRequest(
            username,
            password
        )

        return api.login(request)
    }
}