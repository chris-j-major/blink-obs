package com.light

import com.thingm.blink1.Blink1
import com.thingm.blink1.Blink1Finder
import com.light.config.LightsConfig
import org.slf4j.Logger

class Lights(config: LightsConfig) {
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private val device: Blink1? = if(config.connectAny){
        Blink1Finder.openFirst()
    }else{
        Blink1Finder.openBySerial(config.id)
    }

    init {
        val found = Blink1Finder.listAll()
        log.info("enumerating lights: ${found.joinToString(", ")}")
    }

    fun setTo(target: LightPatternOrColor) {
        if ( target is LightPattern ){
            log.debug("Setting pattern to $target")
            device?.playPattern( target.patternLines )

        }else if ( target is LightColor){
            log.debug("Setting color to $target")
            device?.fadeToRGB(100, target.color )
        }
    }

}
