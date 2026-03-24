package com.sky31.gongmultiplatform

object SystemGlobalConfig {

    val HOST: String = System.getenv("SYSTEM_HOST")
    val UPDATE_HOST: String = System.getenv("SYSTEM_UPDATE_HOST")
    val WEB_HOST: String = System.getenv("SYSTEM_WEB_HOST")
    val MAX_RETRY_TIMES: Int = System.getenv("SYSTEM_MAX_RETRY_TIMES").toInt()
    val RETRY_INTERVAL: Long = System.getenv("SYSTEM_RETRY_INTERVAL").toLong()
}