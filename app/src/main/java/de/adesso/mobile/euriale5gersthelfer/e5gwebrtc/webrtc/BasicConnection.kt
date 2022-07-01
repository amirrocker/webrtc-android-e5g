package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WampFlags
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WebRtC
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.WampConnector
import org.json.JSONObject
import org.webrtc.MediaStream

class BasicConnection(
    val myId: String,
    val _targetId: String,
    val wamp: WampConnector,
    val callbacks: ConnectionCallbacks,
) : Connection {

    private val webRtc: WebRtC

    private val callback = object : WebRtCCallbacks {
        override fun onCreateOffer(sdp: String) {
            try {
                val json = JSONObject().apply {
                    put("sdp", sdp)
                    put("type", "offer")
                    wamp.publishOffer(targetId(), this.toString())
                }
            } catch (e: Exception) {
                error(e)
            }
        }

        override fun onCreateAnswer(sdp: String) {
            TODO("Not yet implemented")
        }

        override fun onAddedStream(mediaStream: MediaStream) {
            TODO("Not yet implemented")
        }

        override fun onIceCandidate(sdp: String, sdpMid: String, sdpMLineIndex: Int) {
            TODO("Not yet implemented")
        }
    }

    init {

        this.webRtc = WebRtC()

    }

}