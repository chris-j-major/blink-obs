package com.light

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import com.light.config.ProgramConfig
import java.io.File
import kotlinx.serialization.decodeFromString

@ExperimentalSerializationApi
fun main(args: Array<String>){
    val configSource = args.firstOrNull()
    val config = configSource?.let {
        val file = File( configSource )
        if ( !file.exists() ){
            println("Config file not found $configSource")
            null
        } else if ( !file.canRead() ){
            println("Unable to read $configSource")
            null
        } else {
            val content = file.readText()
            Json.decodeFromString<ProgramConfig>(content)
        }
    } ?: ProgramConfig()

    val c = OBSLink( config.obs )
    val l = Lights( config.light )

    StatusManager( config.scenes , c , l )
    c.triggerCallback()

    while(true) {
        if ( !c.connected) {
            c.start()
        }
        Thread.sleep(1000)
    }
}