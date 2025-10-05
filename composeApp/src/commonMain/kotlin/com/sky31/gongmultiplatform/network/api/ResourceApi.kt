package com.sky31.gongmultiplatform.network.api

import io.ktor.client.statement.HttpResponse

interface ResourceApi {
    suspend fun getApkZip(url: String): HttpResponse
}