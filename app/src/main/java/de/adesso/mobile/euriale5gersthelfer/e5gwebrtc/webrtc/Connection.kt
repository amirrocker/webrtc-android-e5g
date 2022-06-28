package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.WampTargetId

interface Connection {

    fun targetId(): WampTargetId

    fun publishOffer()

    fun receiveOffer(sdp: String)

    fun receiveAnswer(sdp: String)

    fun receiveCandidate(candidate: String, sdpMid: String, sdpMLineIndex: Int)

    fun close()
}
