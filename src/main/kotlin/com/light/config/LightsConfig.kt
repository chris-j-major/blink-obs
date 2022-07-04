package com.light.config

import kotlinx.serialization.Serializable

@Serializable
data class LightsConfig( val id:String="", val connectAny:Boolean=true, val useTrayIcon: Boolean=true ) {

}
