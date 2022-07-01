package com.light

import com.light.config.ObsConfig
import javafx.application.Application.launch
import net.twasi.obsremotejava.OBSRemoteController
import net.twasi.obsremotejava.events.responses.ScenesChangedResponse
import net.twasi.obsremotejava.events.responses.TransitionBeginResponse
import net.twasi.obsremotejava.events.responses.TransitionEndResponse
import net.twasi.obsremotejava.requests.GetVersion.GetVersionResponse
import org.slf4j.Logger

class OBSLink(config: ObsConfig ) {
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(this.javaClass)
    private val obsSocket = OBSRemoteController( config.obsWsUrl , config.debug , config.obsWsPassword )
    private val callbackList = mutableListOf<(OBSLink)->Unit>()
    var connected: Boolean = false
    var currentScene:String? = null
    var streaming: Boolean = false
    var recording: Boolean = false

    init {
        obsSocket.registerConnectCallback { msg->this.connectCallback(msg!!) }
        obsSocket.registerConnectionFailedCallback { msg->this.connectFailedCallback(msg!!) }
        obsSocket.registerScenesChangedCallback { msg->this.sceneChanged(msg!!) }
        obsSocket.registerTransitionBeginCallback { msg->this.startTransition(msg!!) }
        obsSocket.registerTransitionEndCallback { msg->this.endTransition(msg!!) }
        obsSocket.registerRecordingStartedCallback { this.setRecordingState(true) }
        obsSocket.registerRecordingStoppedCallback { this.setRecordingState(false) }
        obsSocket.registerStreamStartedCallback { this.setStreamingState(true) }
        obsSocket.registerStreamStoppedCallback { this.setStreamingState(false) }
        obsSocket.registerDisconnectCallback {  this.disconnected() }
    }

    private fun disconnected() {
        this.connected = false
        triggerCallback()
    }

    private fun setStreamingState( status:Boolean) {
        this.streaming = status
        triggerCallback()
    }

    private fun setRecordingState( status:Boolean) {
        this.recording = status
        triggerCallback()
    }

    fun registerCallback(function: (OBSLink) -> Unit) {
        callbackList.add(function)
    }

    private fun triggerCallback(){
        callbackList.forEach { f -> f( this ) }
    }

    private fun startTransition(msg: TransitionBeginResponse) {
        currentScene = msg.toScene
        triggerCallback()
    }

    private fun endTransition(msg: TransitionEndResponse) {
        currentScene = msg.toScene
        triggerCallback()
    }

    private fun sceneChanged(msg: ScenesChangedResponse) {
        requestCurrentScene()
    }

    private fun requestCurrentScene() {
        obsSocket.getCurrentScene { scene->
            currentScene=scene.name
            triggerCallback()
        }
    }

    private fun connectFailedCallback(msg: String) {
        this.connected = false
        triggerCallback()
    }

    private fun connectCallback(msg: GetVersionResponse) {
        log.info("Connection ok ${msg.obsStudioVersion} ${msg.obsWebsocketVersion} ${msg.status}")
        this.connected = true
        requestCurrentScene()
    }

    fun start(){
        log.info("Starting OBS connection")
        obsSocket.connect()

    }

}