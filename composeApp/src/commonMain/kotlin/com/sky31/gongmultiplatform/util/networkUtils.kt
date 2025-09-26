package com.sky31.gongmultiplatform.util

import com.sky31.gongmultiplatform.network.response.ApiResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

val authMsgMap = mapOf(
    HttpStatusCode.Unauthorized to "账号或密码错误",
    HttpStatusCode.Conflict to "账号未初始化",
    HttpStatusCode.ServiceUnavailable to "教务系统超时",
    HttpStatusCode.GatewayTimeout to "请求超时"
)

val getMsgMap = mapOf(
    HttpStatusCode.Unauthorized to "token无效",
    HttpStatusCode.NonAuthoritativeInformation to "资源过期",
    HttpStatusCode.NotFound to "请求资源不存在",
    HttpStatusCode.Locked to "账户锁定",
    HttpStatusCode.ServiceUnavailable to "教务系统超时",
    HttpStatusCode.GatewayTimeout to "请求超时",
)

suspend inline fun <reified T> safeApiCall(
    apiCall: suspend () -> HttpResponse
): NetworkResult<T> {
    try {
        val response = apiCall()
        val code = response.status

        return when(code) {
            HttpStatusCode.OK -> {
                val body = response.body<ApiResponse<T>>()
                return NetworkResult.Success(
                    code = code,
                    data = body.data
                )
            }

            HttpStatusCode.Conflict,
            HttpStatusCode.ServiceUnavailable,
            HttpStatusCode.GatewayTimeout,
            HttpStatusCode.Unauthorized ->
                NetworkResult.Error(
                    code = code,
                    message = getMsgMap[code] ?: "Unknown error",
                    exception = Exception("")
                )

            else -> NetworkResult.Error(
                code = code,
                message = "未知错误",
                exception = Exception("")
            )
        }

    } catch (e: Exception) {
        return NetworkResult.Error(
            message = e.message ?: "Unknown error",
            exception = e
        )
    }
}

