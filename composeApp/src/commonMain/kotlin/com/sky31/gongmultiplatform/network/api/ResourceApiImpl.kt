package com.sky31.gongmultiplatform.network.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class ResourceApiImpl(
    private val client: HttpClient
): ResourceApi {

    override suspend fun getApkZip(url: String): HttpResponse =
        client.get(url)
}