package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.data.local.dao.AcademicDao
import com.sky31.gongmultiplatform.data.local.domain.AcademicEntity
import com.sky31.gongmultiplatform.data.local.source.AcademicEntitySourceImpl
import com.sky31.gongmultiplatform.model.AcademicData
import com.sky31.gongmultiplatform.model.RankData
import com.sky31.gongmultiplatform.model.ScoreData
import kotlinx.serialization.json.Json

class AcademicDataRepositoryImpl(
    dao: AcademicDao
): AcademicDataRepository {

    private val source = AcademicEntitySourceImpl(dao)

    override suspend fun updateAcademicData(
        totalRank: RankData?,
        compulsoryRank: RankData?,
        majorScore: ScoreData?,
        minorScore: ScoreData?
    ) {
        val oldEntity = source.getAcademicEntity()

        val entity = AcademicEntity(
            totalRank = totalRank?.let { Json.encodeToString(it) } ?: oldEntity?.totalRank,
            compulsoryRank = compulsoryRank?.let { Json.encodeToString(it) } ?: oldEntity?.compulsoryRank,
            majorScore = majorScore?.let { Json.encodeToString(it) } ?: oldEntity?.majorScore,
            minorScore = minorScore?.let { Json.encodeToString(it) } ?: oldEntity?.minorScore,
        )

        source.insertAcademicEntity(entity)
    }

    override suspend fun getAcademicData(): AcademicData? {
        val entity = source.getAcademicEntity()

        return entity?.let { AcademicData(
            totalRank = it.totalRank?.let { rank -> Json.decodeFromString(rank) },
            compulsoryRank = it.compulsoryRank?.let { rank -> Json.decodeFromString(rank) },
            majorScore = it.majorScore?.let { score -> Json.decodeFromString(score) },
            minorScore = it.minorScore?.let { score -> Json.decodeFromString(score) }
        ) }
    }

    override suspend fun deleteAllAcademicData() {
        source.deleteAllAcademicEntities()
    }
}