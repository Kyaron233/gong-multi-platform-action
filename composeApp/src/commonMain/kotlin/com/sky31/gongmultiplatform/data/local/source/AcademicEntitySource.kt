package com.sky31.gongmultiplatform.data.local.source

import com.sky31.gongmultiplatform.data.local.domain.AcademicEntity

interface AcademicEntitySource {
    suspend fun insertAcademicEntity(entity: AcademicEntity)

    suspend fun updateAcademicEntity(entity: AcademicEntity)

    suspend fun getAcademicEntity(): AcademicEntity?

    suspend fun deleteAllAcademicEntities()
}