package com.light.config

import kotlinx.serialization.Serializable

@Serializable
data class ObsConfig(
    val obsWsUrl:String = "ws://localhost:4444",
    val obsWsPassword:String = "quelea",
    val debug: Boolean = false
)