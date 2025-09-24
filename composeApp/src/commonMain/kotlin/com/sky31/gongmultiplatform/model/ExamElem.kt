package com.sky31.gongmultiplatform.model

import kotlinx.serialization.Serializable

@Serializable
data class ExamElem(
    val name: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val type: String,
)
