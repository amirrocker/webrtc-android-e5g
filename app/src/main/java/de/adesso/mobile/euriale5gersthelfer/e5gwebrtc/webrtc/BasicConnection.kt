package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.Wamp

class BasicConnection(
    val myId: String,
    val _targetId: String,
    val wamp: Wamp,
    val callbacks: ConnectionCallbacks,
) : Connection {

    private val webRtc:

}