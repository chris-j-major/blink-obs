package com.light

import com.light.config.LightsConfig
import com.thingm.blink1.Blink1
import com.thingm.blink1.Blink1Finder
import org.slf4j.Logger


class Lights(config: LightsConfig) {
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private val device: Blink1? = if(config.connectAny){
        Blink1Finder.openFirst()
    }else{
        Blink1Finder.openBySerial(config.id)
    }
    init {
        if ( device == null ){
            log.error("No blink(1) device found...")
            val found = Blink1Finder.listAll()
            log.info("enumerating lights: ${found.joinToString(", ")}")
        }else{
            log.info("firmware: ${device.firmwareVersion} hardware: ${device.version} serial: ${device.serialNumber}")
        }
    }

    fun setTo(target: LightPatternOrColor) {
        if ( target is LightPattern ){
            log.warn("Setting pattern to $target")
            device?.playPattern( target.patternLines )

        }else if ( target is LightColor){
            log.warn("Setting color to $target")
            device?.fadeToRGB(100, target.color )
        }
    }

}
