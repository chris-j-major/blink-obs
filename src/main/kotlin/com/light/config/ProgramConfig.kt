package com.light.config

import kotlinx.serialization.Serializable

@Serializable
data class ProgramConfig(
    val obs:ObsConfig = ObsConfig(),
    val light:LightsConfig = LightsConfig(),
    val scenes:ScenesConfig = ScenesConfig()
)