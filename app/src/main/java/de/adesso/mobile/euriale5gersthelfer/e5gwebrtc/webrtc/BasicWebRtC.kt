package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.ErrorMessage.InvalidPeerConnection
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.ErrorMessage.InvalidSessionDescription
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WebRtC.StunUri
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.VideoCapturer
import org.webrtc.VideoRenderer
import org.webrtc.VideoTrack

class BasicWebRtC(
    val callbacks: WebRtCCallbacks
) : WebRtC, PeerConnection.Observer {

    private abstract class SkeletalSdpObserver : SdpObserver {
        override fun onCreateSuccess(p0: SessionDescription?) {}

        override fun onSetSuccess() {}

        override fun onCreateFailure(p0: String?) {}

        override fun onSetFailure(p0: String?) {}
    }

    private var peerConnection: PeerConnection? = null

    init {
        // create peer connection
        // stun servers from google for voip comms
        // see: https://groups.google.com/g/discuss-webrtc/c/b-5alYpbxXw
        // and http://code.google.com/p/natvpn/source/browse/trunk/stun_server_list
        val iceServers = listOf(PeerConnection.IceServer(StunUri))
        factory?.let {
            peerConnection =
                it.createPeerConnection(iceServers, WebRtCUtil.peerConnectionConstraints(), this)
            peerConnection?.addStream(localStream) ?: println(InvalidPeerConnection)
        } ?: println("factory is null!")
    }

    /*
    * PeerConnection.Observer implementation
    */
    override fun onIceCandidate(p0: IceCandidate?) {
        val iceCandidate = p0 ?: error("could not establish ice candiate")
        callbacks.onIceCandidate(iceCandidate.sdp, iceCandidate.sdpMid, iceCandidate.sdpMLineIndex)
    }

    override fun onAddStream(p0: MediaStream?) {
        val stream = p0 ?: error("stream is null")
        callbacks.onAddedStream(stream)
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) =
        println("onSignalingChange")

    override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) =
        println("onIceConnectionChange")

    override fun onIceConnectionReceivingChange(p0: Boolean) =
        println("onIceConnectionReceivingChange")

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) =
        println("onIceGatheringChange")

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) =
        println("onIceCandidatesRemoved")

    override fun onRemoveStream(p0: MediaStream?) =
        println("onRemoveStream")

    override fun onDataChannel(p0: DataChannel?) =
        println("onDataChannel")

    override fun onRenegotiationNeeded() =
        println("onRenegotiationNeeded")

    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) =
        println("onAddTrack")

    override fun receiveCandidate(sdp: String, sdpMid: String, sdpMLineIndex: Int) {
        peerConnection?.let { connection ->
            connection.addIceCandidate(IceCandidate(sdpMid, sdpMLineIndex, sdp))
        } ?: error(InvalidPeerConnection)
    }

    // TODO remove this "callback hell"!
    override fun receiveOffer(sdp: String) {
        // remote description
        val remoteDescription = SessionDescription(SessionDescription.Type.OFFER, sdp)
        peerConnection?.let { connection ->
            connection.setRemoteDescription(
                object : SkeletalSdpObserver() {
                    override fun onSetSuccess() {
                        connection.createAnswer(
                            object : SkeletalSdpObserver() {
                                override fun onCreateSuccess(p0: SessionDescription?) {
                                    connection.setLocalDescription(
                                        object : SkeletalSdpObserver() {
                                            override fun onSetSuccess() {
                                                callbacks.onCreateAnswer(
                                                    p0?.description ?: error(
                                                        InvalidSessionDescription
                                                    )
                                                )
                                            }
                                        },
                                        p0
                                    )
                                }
                            },
                            WebRtCUtil.answerConnectionConstraints()
                        )
                    }

                    override fun onSetFailure(p0: String?) {
                        val error = p0 ?: "WebRtC error[onSetFailure] encountered."
                        error(error)
                    }
                },
                remoteDescription
            )
        } ?: error(InvalidPeerConnection)
    }

    // TODO not as bad as the former but still rem callback hell here also
    override fun createOffer() =
        peerConnection?.let { connection ->
            println("createOffer called")
            connection.createOffer(
                object : SkeletalSdpObserver() {
                    override fun onCreateSuccess(p0: SessionDescription?) {
                        peerConnection?.let { innerPeerConnection ->
                            innerPeerConnection.setLocalDescription(
                                object : SkeletalSdpObserver() {
                                    override fun onSetSuccess() {
                                        callbacks.onCreateOffer(
                                            p0?.description
                                                ?: error(InvalidSessionDescription)
                                        )
                                    }
                                },
                                p0
                            )
                        }
                    }
                },
                WebRtCUtil.offerConnectionConstraints()
            )
        } ?: error(InvalidPeerConnection)

    override fun receiveAnswer(sdp: String) =
        peerConnection?.let { connection ->
            connection.setRemoteDescription(
                object : SkeletalSdpObserver() {
                    override fun onSetSuccess() {
                        println("onSetSuccess called")
                    }
                },
                SessionDescription(SessionDescription.Type.ANSWER, sdp)
            )
        } ?: error(InvalidPeerConnection)

    override fun close() {
        peerConnection?.let {
            it.removeStream(localStream)
            it.close()
        }
        peerConnection = null
    }

    companion object {

        private var factory: PeerConnectionFactory? = null

        internal var localStream: MediaStream? = null

        private var videoCapturer: VideoCapturer? = null

        private var eglBase: EglBase? = null

        private var localVideoTrack: VideoTrack? = null

        private val localRenderer: VideoRenderer? = null

        internal fun setup(activity: Activity, eglBase: EglBase) {
            BasicWebRtC.eglBase = eglBase

            // init factory
//            val options = PeerConnectionFactory.Options()
//            PeerConnectionFactory.initializeAndroidGlobals(activity.applicationContext, true)
//            factory = PeerConnectionFactory(options)
//            factory!!.setVideoHwAccelerationOptions(eglBase.eglBaseContext, eglBase.eglBaseContext)

            initializePeerConnectionFactory(activity)

//            val localStream = factory!!.createLocalMediaStream("TEST_LOCAL_ANDROID_STREAM")
//            this.localStream = localStream

            initializeLocalStream()

//            // setup the different tracks - video and audio
//            videoCapturer = createCameraCapturer(Camera2Enumerator(activity))
//            val localVideoSource = factory!!.createVideoSource(videoCapturer)
//            localVideoTrack = factory!!.createVideoTrack("TEST_LOCAL_ANDROID_STREAM", localVideoSource)
//            localStream.addTrack(localVideoTrack)

            initializeTracks(activity)

//            // initialize DisplayMetrics
//            val displayMetrics = DisplayMetrics()
//            val windowManager = activity.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//            // how to avoid deprecation ?
//            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
//            val videoWidth = displayMetrics.widthPixels
//            val videoHeight = displayMetrics.heightPixels

            val widthHeight:Pair<Int, Int> = initializeDisplayMetrics(activity)

            videoCapturer?.let {
                it.startCapture(widthHeight.first, widthHeight.second, 30)
            }
        }

        private fun initializeDisplayMetrics(activity: Activity): Pair<Int, Int> {
            // initialize DisplayMetrics
            val displayMetrics = DisplayMetrics()
            val windowManager = activity.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            // how to avoid deprecation ?
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            val videoWidth = displayMetrics.widthPixels
            val videoHeight = displayMetrics.heightPixels
            return Pair(videoWidth, videoHeight)
        }

        private fun initializeTracks(activity: Activity) {
            initializeVideoTrack(activity)

            initializeAudioTrack(activity)
        }

        private fun initializeAudioTrack(activity: Activity) {
            val audioSource = factory!!.createAudioSource(WebRtCUtil.mediaStreamConstraints())
            val audioTrack = factory!!.createAudioTrack("TEST_LOCAL_ANDROID_AUDIO_TRACK", audioSource)
            localStream.addTrack(audioTrack)
        }

        private fun initializeVideoTrack(activity: Activity) {
            // setup the different tracks - video and audio
            videoCapturer = createCameraCapturer(Camera2Enumerator(activity))
            val localVideoSource = factory!!.createVideoSource(videoCapturer)
            localVideoTrack = factory!!.createVideoTrack("TEST_LOCAL_ANDROID_VIDEO_TRACK", localVideoSource)
            localStream?.addTrack(localVideoTrack)
        }

        private fun createCameraCapturer(cameraEnumerator: CameraEnumerator):VideoCapturer? =
            createBackCameraCapturer(cameraEnumerator)


        private fun initializeLocalStream() {
            this.localStream = factory!!.createLocalMediaStream("TEST_LOCAL_ANDROID_STREAM")
        }

        private fun createBackCameraCapturer(cameraEnumerator: CameraEnumerator): VideoCapturer? {
            val deviceNames = cameraEnumerator.deviceNames

            deviceNames.forEach {
                if(cameraEnumerator.isBackFacing(it)) {
                    val capturer = cameraEnumerator.createCapturer(it, null)
                    if(capturer != null) return capturer
                }
            }
            return null
        }

        private fun initializePeerConnectionFactory(activity: Activity) {
            val options = PeerConnectionFactory.Options()
            PeerConnectionFactory.initializeAndroidGlobals(activity.applicationContext, true)
            factory = PeerConnectionFactory(options).apply {
                this.setVideoHwAccelerationOptions(eglBase?.eglBaseContext, eglBase?.eglBaseContext)
            }
        }

    }
}
