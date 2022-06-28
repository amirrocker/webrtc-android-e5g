package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WebRtC.OfferToReceiveAudio
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WebRtC.OfferToReceiveVideo
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WebRtC.TrueValue
import org.webrtc.MediaConstraints

object WebRtCUtil {

    internal fun peerConnectionConstraints(): MediaConstraints =
        audioVideoConstraints()

    internal fun offerConnectionConstraints(): MediaConstraints =
        audioVideoConstraints()

    internal fun answerConnectionConstraints(): MediaConstraints =
        audioVideoConstraints()

    internal fun mediaStreamConstraints(): MediaConstraints = MediaConstraints()

    private fun audioVideoConstraints(): MediaConstraints = MediaConstraints().apply {
        mandatory.add(
            MediaConstraints.KeyValuePair(OfferToReceiveAudio, TrueValue)
        )
        mandatory.add(
            MediaConstraints.KeyValuePair(OfferToReceiveVideo, TrueValue)
        )
    }
}
