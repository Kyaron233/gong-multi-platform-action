package com.sky31.gongmultiplatform.data.repository

import com.sky31.gongmultiplatform.data.local.dao.PublicDao
import com.sky31.gongmultiplatform.data.local.domain.PublicEntity
import com.sky31.gongmultiplatform.data.local.source.PublicEntitySourceImpl
import com.sky31.gongmultiplatform.model.CalendarData
import com.sky31.gongmultiplatform.model.ClassroomData
import kotlinx.serialization.json.Json

class PublicDataRepositoryImpl(
    dao: PublicDao
): PublicDataRepository {

    private val source = PublicEntitySourceImpl(dao)

    override suspend fun insertPublicData(
        todayClassroom: ClassroomData?,
        tomorrowClassroom: ClassroomData?,
        calendar: CalendarData?
    ) {
        val entity = PublicEntity(
            todayClassroom = todayClassroom?.let { Json.encodeToString<ClassroomData>(it) },
            tomorrowClassroom = tomorrowClassroom?.let { Json.encodeToString<ClassroomData>(it) },
            calendar = calendar?.let { Json.encodeToString<CalendarData>(it) }
        )

        source.insertPublicEntity(entity)
    }

    override suspend fun updatePublicData(
        todayClassroom: ClassroomData?,
        tomorrowClassroom: ClassroomData?,
        calendar: CalendarData?
    ) {
        val oldEntity = source.getPublicEntity()

        val entity = PublicEntity(
            todayClassroom = todayClassroom?.let { Json.encodeToString<ClassroomData>(it) } ?: oldEntity?.todayClassroom,
            tomorrowClassroom = tomorrowClassroom?.let { Json.encodeToString<ClassroomData>(it) } ?: oldEntity?.tomorrowClassroom,
            calendar = calendar?.let { Json.encodeToString<CalendarData>(it) } ?: oldEntity?.calendar
        )

        source.updatePublicEntity(entity)
    }

    override suspend fun getCalendar(): CalendarData? {
        val calendar = source.getCalendar()

        return calendar?.let { Json.decodeFromString<CalendarData>(it) }
    }

    override suspend fun getTodayClassroom(): ClassroomData? {
        val todayClassroom = source.getTodayClassroom()

        return todayClassroom?.let { Json.decodeFromString<ClassroomData>(it) }
    }

    override suspend fun getTomorrowClassroom(): ClassroomData? {
        val tomorrowClassroom = source.getTomorrowClassroom()

        return tomorrowClassroom?.let { Json.decodeFromString<ClassroomData>(it) }
    }

    override suspend fun deleteAllPublicData() {
        source.deleteAllPublicEntities()
    }
}