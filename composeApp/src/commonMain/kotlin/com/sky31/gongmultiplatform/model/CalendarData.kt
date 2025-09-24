package com.sky31.gongmultiplatform.model

import kotlinx.serialization.Serializable

@Serializable
data class CalendarData(
    val start: String,
    val weeks: Int,
    val termId: String
)
