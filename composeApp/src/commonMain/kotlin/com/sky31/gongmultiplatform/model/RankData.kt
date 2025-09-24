package com.sky31.gongmultiplatform.model

import kotlinx.serialization.Serializable

@Serializable
data class RankData(
    // 平均成绩
    val averageScore: String,
    // 绩点
    val gpa: String,
    // 班级排名
    val classRank: Int,
    // 专业排名
    val majorRank: Int,
    // 学期
    val terms: List<String>
)
