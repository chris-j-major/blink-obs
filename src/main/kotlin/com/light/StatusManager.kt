package com.light

import com.light.config.ScenesConfig
import org.slf4j.Logger

class StatusManager(private val scenes: ScenesConfig, c: OBSLink, l: List<StateReceiver>) {
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)

    private var currentColour: LightPatternOrColor? = null

    init {
        c.registerCallback { control: OBSLink ->
            val nextColor = scenes.chooseColor(control)
            if (nextColor != currentColour) {
                log.warn("Switching to $nextColor")
                l.forEach { it.setTo(nextColor) }
                currentColour = nextColor
            }
        }
    }
}

