package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

interface WampCallbacks {

    fun onOpen()

    fun onReceiveAnswer(targetId: WampTargetId, sdp: String)

    fun onReceiveOffer(targetId: WampTargetId, sdp: String)

    fun onIceCandidate(targetId: WampTargetId, sdp: String, sdpMid: String, sdpMLineIndex: Int)

    fun onReceiveCallme(targetId: WampTargetId)

    fun onCloseConnection(targetId: WampTargetId)
}