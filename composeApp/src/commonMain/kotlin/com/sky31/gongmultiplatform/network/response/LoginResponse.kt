package com.sky31.gongmultiplatform.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: Boolean,
    @SerialName("expires_in") val expiresIn: String,
    @SerialName("token_type") val tokenType: String
)
