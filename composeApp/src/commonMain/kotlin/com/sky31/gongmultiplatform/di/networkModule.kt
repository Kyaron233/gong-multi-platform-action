package com.sky31.gongmultiplatform.di

import com.sky31.gongmultiplatform.network.HttpClientProvider
import com.sky31.gongmultiplatform.network.repository.AcademicRepositoryImpl
import com.sky31.gongmultiplatform.network.repository.AuthRepositoryImpl
import com.sky31.gongmultiplatform.network.repository.CourseRepositoryImpl
import com.sky31.gongmultiplatform.network.repository.ExamRepositoryImpl
import com.sky31.gongmultiplatform.network.repository.PublicRepositoryImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClientProvider.client

        AcademicRepositoryImpl(get<HttpClient>())

        ExamRepositoryImpl(get<HttpClient>())

        CourseRepositoryImpl(get<HttpClient>())

        PublicRepositoryImpl(get<HttpClient>())

        AuthRepositoryImpl(get<HttpClient>())
    }
}