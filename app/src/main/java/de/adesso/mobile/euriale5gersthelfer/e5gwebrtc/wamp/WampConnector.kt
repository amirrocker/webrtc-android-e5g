package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

typealias WampTargetId = String
typealias WampUrl = String
typealias WampRoomId = String
typealias WampUserId = String

interface WampConnector {

    fun connect()

    fun publishCallme()

    // TODO look up sdp
    fun publishOffer(targetId: WampTargetId, sdp: String)

    fun publishAnswer(targetId: WampTargetId, sdp: String)

    fun publishCandidate(targetId: WampTargetId, candidate: String)
}
