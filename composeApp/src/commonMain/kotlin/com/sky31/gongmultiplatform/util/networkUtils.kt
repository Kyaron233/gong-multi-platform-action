package com.sky31.gongmultiplatform.util

import com.sky31.gongmultiplatform.SystemGlobalConfig
import com.sky31.gongmultiplatform.network.response.ApiResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

val authMsgMap = mapOf(
    HttpStatusCode.BadRequest to "请求格式错误(400)",
    HttpStatusCode.Unauthorized to "账号或密码错误",
    HttpStatusCode.PaymentRequired to "需要付费访问(402)",
    HttpStatusCode.Forbidden to "请求被拒绝(403)，请检查请求格式或鉴权策略",
    HttpStatusCode.NotFound to "请求的资源不存在(404)",
    HttpStatusCode.MethodNotAllowed to "请求方法不被允许(405)",
    HttpStatusCode.NotAcceptable to "请求无法被接受(406)",
    HttpStatusCode.RequestTimeout to "请求超时(408)",
    HttpStatusCode.Conflict to "账号未初始化",
    HttpStatusCode.Gone to "资源已永久移除(410)",
    HttpStatusCode.LengthRequired to "缺少Content-Length(411)",
    HttpStatusCode.PreconditionFailed to "前置条件失败(412)",
    HttpStatusCode.PayloadTooLarge to "请求体过大(413)",
    HttpStatusCode.UriTooLong to "URI过长(414)",
    HttpStatusCode.UnsupportedMediaType to "不支持的媒体类型(415)",
    HttpStatusCode.RangeNotSatisfiable to "范围请求无法满足(416)",
    HttpStatusCode.ExpectationFailed to "期望失败(417)",
    HttpStatusCode.MisdirectedRequest to "请求被发送到错误的服务器(421)",
    HttpStatusCode.UnprocessableEntity to "请求语义错误(422)",
    HttpStatusCode.Locked to "账户锁定",
    HttpStatusCode.FailedDependency to "依赖请求失败(424)",
    HttpStatusCode.UpgradeRequired to "需要升级协议(426)",
    HttpStatusCode.TooManyRequests to "请求过于频繁(429)",
    HttpStatusCode.RequestHeaderFieldsTooLarge to "请求头字段过大(431)",
    HttpStatusCode.UnavailableForLegalReasons to "因法律原因不可用(451)",
    HttpStatusCode.InternalServerError to "服务器内部错误(500)",
    HttpStatusCode.NotImplemented to "服务器不支持该功能(501)",
    HttpStatusCode.BadGateway to "网关错误",
    HttpStatusCode.ServiceUnavailable to "教务系统超时",
    HttpStatusCode.GatewayTimeout to "请求超时",
    HttpStatusCode.HTTPVersionNotSupported to "HTTP版本不支持(505)",
    HttpStatusCode.InsufficientStorage to "存储空间不足(507)",
    HttpStatusCode.LoopDetected to "服务器检测到循环(508)",
    HttpStatusCode.NotExtended to "需要扩展(510)",
    HttpStatusCode.NetworkAuthenticationRequired to "需要网络认证(511)",
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

        println(code.toString())
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
            HttpStatusCode.Unauthorized,
            HttpStatusCode.NonAuthoritativeInformation -> {
                NetworkResult.Error(
                    code = code,
                    message = getMsgMap[code] ?: "Unknown error",
                    exception = Exception("")
                )
            }

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

fun codeToDataState(code: HttpStatusCode?): DataState {
    return when(code) {
        HttpStatusCode.Unauthorized -> DataState.Unauthorized
        HttpStatusCode.NonAuthoritativeInformation -> DataState.Expired
        HttpStatusCode.Locked -> DataState.Error("账号被锁定")
        HttpStatusCode.ServiceUnavailable -> DataState.Error("教务系统超时")
        HttpStatusCode.GatewayTimeout -> DataState.Error("请求超时")
        else -> DataState.Error("未知错误")
    }
}

suspend fun safeApiCallsSequential(
    calls: List<suspend () -> DataState>
): List<DataState> = coroutineScope {
    val deferredResults = calls.map { call ->
        async {
            var count = 0
            while (count < SystemGlobalConfig.MAX_RETRY_TIMES) {
                val dataState = call()
                when (dataState) {
                    is DataState.Expired -> {
                        count++
                        delay(SystemGlobalConfig.RETRY_INTERVAL)
                    }

                    is DataState.Unauthorized -> {
                        TokenState.expired()
                        return@async dataState
                    }

                    else -> {
                        return@async dataState
                    }
                }
            }

            DataState.Error("请求超时")
        }
    }

    deferredResults.awaitAll()
}

fun checkResults(results: List<DataState>): Boolean {
    for(result in results) {
        if(result != DataState.Newest) return false
    }

    return true
}

object TokenState {
    private val _isExpired = MutableStateFlow(false)
    val isExpired = _isExpired.asStateFlow()

    fun expired() {
        _isExpired.value = true
    }

    fun refreshed() {
        _isExpired.value = false
    }
}

