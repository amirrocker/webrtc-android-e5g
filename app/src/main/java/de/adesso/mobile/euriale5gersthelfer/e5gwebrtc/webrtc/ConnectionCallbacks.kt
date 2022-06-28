package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc

import org.webrtc.MediaStream

interface ConnectionCallbacks {

    fun onAddedStream(mediaStream: MediaStream)
}