package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

interface WebRtC {

    fun createOffer()

    fun receiveOffer(sdp: String)

    fun receiveAnswer(sdp: String)

    fun receiveCandidate(sdp: String, sdpMid: String, sdpMLineIndex: Int)

    fun close()
}
