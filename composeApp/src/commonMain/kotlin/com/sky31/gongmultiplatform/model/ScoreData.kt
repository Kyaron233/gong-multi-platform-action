package com.sky31.gongmultiplatform.model

import kotlinx.serialization.Serializable

@Serializable
data class ScoreData(
    val studentId: String,
    val name: String,
    val college: String,
    val major: String,
    val scores: List<ScoreElem>,
    val totalCredit: List<String>,
    val electiveCredit: List<String>,
    val compulsoryCredit: List<String>,
    val crossCourseCredit: List<String>,
    val averageScore: String,
    val gpa: String,
    val cet4: String,
    val cet6: String
) {
    @Serializable
    data class ScoreElem(
        val name: String,
        val score: String,
        val credit: String,
        val type: String,
        val term: Int
    )
}

