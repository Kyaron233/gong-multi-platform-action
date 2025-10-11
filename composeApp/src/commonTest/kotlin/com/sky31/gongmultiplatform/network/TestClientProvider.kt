package com.sky31.gongmultiplatform.network

import com.sky31.gongmultiplatform.SystemGlobalConfig
import com.sky31.gongmultiplatform.model.TokenData
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object TestClientProvider {

    fun createClient(
        engine: MockEngine
    ): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(Resources)

        install(Auth) {
            bearer {
                loadTokens {
                    TokenData.token?.let { BearerTokens(it, "") }
                }
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.Companion.HTTP
            }
//            contentType(ContentType.Application.Json)
        }
    }

    fun createIntegrationTest(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }

        install(Resources)

        install(Auth) {
            bearer {
                loadTokens {
                    TokenData.token?.let { BearerTokens(it, "") }
                }
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.Companion.HTTP
                host = SystemGlobalConfig.HOST
            }
        }
    }
}