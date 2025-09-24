package com.sky31.gongmultiplatform.model

import kotlinx.serialization.Serializable

@Serializable
data class CourseElem(
    val name: String,
    val teacher: String,
    val classroom: String,
    val weeks: String,
    val startTime: Int,
    val duration: Int,
    val day: String
)
