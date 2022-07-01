package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc

object WampFlags {
    // template endpoint:
    // "wss://nakadoribooks-webrtc.herokuapp.com"
    const val HandshakeEndpoint = "https://mec.euriale.de/someId/api/data/webrtcsignals"
    const val RealmOne = "realm1"
}

object WebRtC {
    const val OfferToReceiveAudio = "OfferToReceiveAudio"
    const val OfferToReceiveVideo = "OfferToReceiveVideo"
    const val TrueValue = "true"
    const val StunUri = "stun:stun.l.google.com:19302"
}

object ErrorMessage {
    const val InvalidPeerConnection = "peerConnection is null!"
    const val InvalidSessionDescription = "sessionDescription is empty or null!"
}

object GridLayout {
    const val GRID_LAYOUT_WIDTH = 500
    const val GRID_LAYOUT_HEIGHT = 500
    const val GRID_LAYOUT_LEFT_MARGIN = 10
    const val GRID_LAYOUT_RIGHT_MARGIN = 10
    const val GRID_LAYOUT_TOP_MARGIN = 10
}
