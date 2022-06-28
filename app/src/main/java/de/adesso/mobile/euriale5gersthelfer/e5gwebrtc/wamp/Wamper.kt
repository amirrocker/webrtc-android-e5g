package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

import android.app.Activity

class Wamper(
    val activity: Activity,
    val roomId: WampRoomId = "abcdef"
) {

    private var wamp: WampConnector? = null // is it necessary to be nullable ?

    fun setup() {

        wamp = BasicWamp(
            activity, "roomId", "userId",
            object : WampCallbacks {
                override fun onOpen() {
                    TODO("Not yet implemented")
                }

                override fun onReceiveAnswer(targetId: WampTargetId, sdp: String) {
                    TODO("Not yet implemented")
                }

                override fun onReceiveOffer(targetId: WampTargetId, sdp: String) {
                    TODO("Not yet implemented")
                }

                override fun onIceCandidate(
                    targetId: WampTargetId,
                    sdp: String,
                    sdpMid: String,
                    sdpMLineIndex: Int
                ) {
                    TODO("Not yet implemented")
                }

                override fun onReceiveCallme(targetId: WampTargetId) {
                    TODO("Not yet implemented")
                }

                override fun onCloseConnection(targetId: WampTargetId) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
