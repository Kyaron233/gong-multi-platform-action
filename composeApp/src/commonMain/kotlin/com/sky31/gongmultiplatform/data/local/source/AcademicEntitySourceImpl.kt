package com.sky31.gongmultiplatform.data.local.source

import com.sky31.gongmultiplatform.data.local.dao.AcademicDao
import com.sky31.gongmultiplatform.data.local.domain.AcademicEntity

class AcademicEntitySourceImpl(
    private val dao: AcademicDao
): AcademicEntitySource {

    override suspend fun insertAcademicEntity(entity: AcademicEntity) {
        dao.insertAcademicEntity(entity)
    }

    override suspend fun getAcademicEntity(): AcademicEntity? {
        return dao.getAcademicEntity()
    }

    override suspend fun updateAcademicEntity(entity: AcademicEntity) {
        dao.updateAcademicEntity(entity)
    }

    override suspend fun deleteAllAcademicEntities() {
        dao.clearAll()
    }
}