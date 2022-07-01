package com.light.config

import kotlinx.serialization.Serializable
import com.light.OBSLink
import com.light.LightPatternOrColor
import com.light.LightPatternOrColorHelper.colorOrPatternFromString
import org.slf4j.Logger

@Serializable
data class ScenesConfig(
    val known:List<SceneMatch> = listOf(
        SceneMatch(".*[sS]ong.*","#f00ff00"),
        SceneMatch(".*[cC]amera.*","#f0000")
    ),
    val noConnection:String = "#ff0000:50,#000000:100",
    val notStreamingNotRecording:String = "#ff0000:100,#000000:100",
    val notStreaming:String = "#ff0000:100,#0000ff:100",
    val notRecording:String = "#ff0000:100,#00ff00:100",
    val unknownScene:String = "#000000",
    val noScene:String = "#000000",
    val shouldBeRecording:Boolean = false,
    val shouldBeStreaming:Boolean = false,
) {
    @kotlinx.serialization.Transient
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)
    @kotlinx.serialization.Transient
    private val noConnectionC = colorOrPatternFromString(noConnection)
    @kotlinx.serialization.Transient
    private val notStreamingNotRecordingC = colorOrPatternFromString(notStreamingNotRecording)
    @kotlinx.serialization.Transient
    private val notStreamingC = colorOrPatternFromString(notStreaming)
    @kotlinx.serialization.Transient
    private val notRecordingC = colorOrPatternFromString(notRecording)
    @kotlinx.serialization.Transient
    private val unknownSceneC = colorOrPatternFromString(unknownScene)
    @kotlinx.serialization.Transient
    private val noSceneC = colorOrPatternFromString(noScene)

    fun chooseColor(control: OBSLink): LightPatternOrColor {
        return if (!control.connected) {
            noConnectionC
        } else if (!control.streaming && shouldBeStreaming ) {
            if (!control.recording && shouldBeRecording  ) {
                notStreamingNotRecordingC
            } else {
                notStreamingC
            }
        } else if (!control.recording&& shouldBeStreaming  ) {
            notRecordingC
        } else {
            matchScene(control.currentScene)
        }
    }

    private fun matchScene(currentScene: String?): LightPatternOrColor {
        if (currentScene.isNullOrBlank()) return noSceneC
        known.forEach { match ->
            if (match.matches(currentScene)){
                log.info("Found match for $currentScene in ${match.regex}")
                return match.output
            }
        }
        log.warn("No matches found - using unknownScene")
        return unknownSceneC
    }
}

@Serializable
data class SceneMatch(val regex: String, val lights: String) {

    @kotlinx.serialization.Transient
    private val regexMatcher = Regex(regex)
    @kotlinx.serialization.Transient
    val output = colorOrPatternFromString(lights)

    fun matches(currentScene: String): Boolean {
        return regexMatcher.matchEntire(currentScene) != null
    }

}
