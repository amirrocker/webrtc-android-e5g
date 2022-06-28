package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import android.app.Activity
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.ErrorMessage.InvalidPeerConnection
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WebRtC.StunUri
import org.webrtc.EglBase
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
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

    override fun receiveOffer(sdp: String) {
        // remote description
        val remoteDescription = SessionDescription
    }

    override fun createOffer() =
        peerConnection?.let { connection ->
            println("createOffer called")
            connection.createOffer(
                object : SkeletalSdpObserver() {
                    override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                        peerConnection?.let { innerPeerConnection ->
                            innerPeerConnection.setLocalDescription(object : SkeletalSdpObserver() {
                                override fun onSetSuccess() {
                                    callbacks.onCreateOffer(
                                        sessionDescription?.description
                                            ?: error("sessionDescription.description is null")
                                    )
                                }
                            })
                        }
                    }
                },
                WebRtCUtil.offerConnectionConstraints()
            )
        } ?: error(InvalidPeerConnection)

    override fun receiveAnswer(sdp: String) {
        TODO("Not yet implemented")
    }

    override fun receiveCandidate(sdp: String, sdpMid: String, sdpMLineIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
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
        }
    }
}
