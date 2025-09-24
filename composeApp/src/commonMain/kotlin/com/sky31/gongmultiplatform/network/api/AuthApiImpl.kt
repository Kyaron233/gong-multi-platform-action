package com.sky31.gongmultiplatform.network.api

import com.sky31.gongmultiplatform.network.request.LoginRequest
import com.sky31.gongmultiplatform.network.resources.Login
import com.sky31.gongmultiplatform.network.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody

class AuthApiImpl(
    private val client: HttpClient
): AuthApi {

    override suspend fun login(request: LoginRequest): LoginResponse =
        client.post(Login()) {
            setBody(request)
        }.body()

}