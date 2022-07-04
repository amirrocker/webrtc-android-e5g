package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.WampConnector
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.WampTargetId
import org.json.JSONObject
import org.webrtc.MediaStream

class BasicConnection(
    val myId: String,
    val _targetId: String,
    val wamp: WampConnector,
    val callbacks: ConnectionCallbacks,
) : Connection {

    private val webRtc: WebRtC

    /**
     * callbacks for WebRtC Connections
     */
    private val webRtCCallbacks = object : WebRtCCallbacks {
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
            // doublette!
            try {
                val json = JSONObject().apply {
                    put("sdp", sdp)
                    put("type", "answer")
                    wamp.publishOffer(targetId(), this.toString())
                }
            } catch (e: Exception) {
                error(e)
            }
        }

        override fun onAddedStream(mediaStream: MediaStream) =
            callbacks.onAddedStream(mediaStream)

        override fun onIceCandidate(sdp: String, sdpMid: String, sdpMLineIndex: Int) {
            try {
                val json = JSONObject().apply {
                    put("type", "candidate")
                    put("candidate", sdp)
                    put("sdpMid", sdpMid)
                    put("sdpMLineIndex", sdpMLineIndex)
                    wamp.publishCandidate(targetId(), this.toString())
                }
            } catch (e: Exception) {
                error(e)
            }
        }
    }

    init {
        this.webRtc = BasicWebRtC(callbacks = webRtCCallbacks)
    }

    // interface implementation
    override fun targetId(): WampTargetId = _targetId

    override fun publishOffer() = webRtc.createOffer()

    override fun receiveOffer(sdp: String) = webRtc.receiveOffer(sdp)

    override fun receiveAnswer(sdp: String) = webRtc.receiveAnswer(sdp)

    override fun receiveCandidate(candidate: String, sdpMid: String, sdpMLineIndex: Int) =
        webRtc.receiveCandidate(candidate, sdpMid, sdpMLineIndex)

    override fun close() = webRtc.close()
}
