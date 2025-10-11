package com.sky31.gongmultiplatform.model.config

import kotlinx.serialization.Serializable

@Serializable
data class FunctionalConfig(
    val reauthentication: Boolean = true
)
