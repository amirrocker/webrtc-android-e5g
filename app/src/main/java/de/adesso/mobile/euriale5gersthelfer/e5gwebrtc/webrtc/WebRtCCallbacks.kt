package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import org.webrtc.MediaStream

interface WebRtCCallbacks {

    fun onCreateOffer(sdp: String)

    fun onCreateAnswer(sdp: String)

    fun onAddedStream(mediaStream: MediaStream)

    fun onIceCandidate(sdp: String, sdpMid: String, sdpMLineIndex: Int)
}
