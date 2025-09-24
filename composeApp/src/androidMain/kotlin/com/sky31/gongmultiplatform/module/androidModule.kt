package com.sky31.gongmultiplatform.module

import com.sky31.gongmultiplatform.data.local.AppDatabase
import com.sky31.gongmultiplatform.data.local.getAppDatabase
import com.sky31.gongmultiplatform.data.repository.AcademicDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.ConfigRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.CourseDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.ExamDataRepositoryImpl
import com.sky31.gongmultiplatform.data.repository.PublicDataRepositoryImpl
import com.sky31.gongmultiplatform.db.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single {
        getAppDatabase(
            getDatabaseBuilder(androidContext())
        )

        AcademicDataRepositoryImpl(
            dao = get<AppDatabase>().getAcademicDao()
        )

        CourseDataRepositoryImpl(
            dao = get<AppDatabase>().getCourseDao()
        )

        ExamDataRepositoryImpl(
            dao = get<AppDatabase>().getExamDao()
        )

        PublicDataRepositoryImpl(
            dao = get<AppDatabase>().getPublicDao()
        )

        ConfigRepositoryImpl(
            dao = get<AppDatabase>().getConfigDao()
        )
    }
}