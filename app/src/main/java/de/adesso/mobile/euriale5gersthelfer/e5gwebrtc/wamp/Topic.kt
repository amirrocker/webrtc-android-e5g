package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

/*
* I assume that we will end up with an url that is similar shaped like:
* https://mec.euriale.de/SoME-HaSh-WiTH-nUmBerS/api/webrtc/123456789/callme
* or something. Question is whether we use a room based routing system or
* a stream based routing system. Currently we use the former but need to
* align with server.
*/
internal enum class Topic(val url: WampUrl) {

    Callme("de.e5g.webrtc.[roomId].callme"),
    Close("de.e5g.webrtc.[roomId].close"),
    Answer("de.e5g.webrtc.[roomId].[userId].answer"),
    Offer("de.e5g.webrtc.[roomId].[userId].offer"),
    Candidate("de.e5g.webrtc.[roomId].[userId].candidate"),
}
