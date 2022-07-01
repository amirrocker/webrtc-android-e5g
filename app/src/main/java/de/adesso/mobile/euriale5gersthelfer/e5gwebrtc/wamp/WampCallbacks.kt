package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

import org.webrtc.IceCandidate

interface WampCallbacks {

    fun onOpen()

    fun onReceiveAnswer(targetId: WampTargetId, sdp: String)

    fun onReceiveOffer(targetId: WampTargetId, sdp: String)

    fun onIceCandidate(targetId: WampTargetId, candidate: String, sdpMid: String, sdpMLineIndex: Int)

    fun onReceiveCallme(targetId: WampTargetId)

    fun onCloseConnection(targetId: WampTargetId)
}