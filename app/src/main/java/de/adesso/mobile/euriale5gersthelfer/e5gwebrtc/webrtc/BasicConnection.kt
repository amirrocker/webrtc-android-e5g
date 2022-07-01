package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WampFlags
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp.WampConnector

class BasicConnection(
    val myId: String,
    val _targetId: String,
    val wamp: WampConnector,
    val callbacks: ConnectionCallbacks,
) : Connection {

    private val webRtc:

}