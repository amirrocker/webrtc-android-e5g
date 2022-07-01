package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

import android.app.Activity
import android.widget.GridLayout
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.GridLayout.GRID_LAYOUT_HEIGHT
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.GridLayout.GRID_LAYOUT_LEFT_MARGIN
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.GridLayout.GRID_LAYOUT_RIGHT_MARGIN
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.GridLayout.GRID_LAYOUT_TOP_MARGIN
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.GridLayout.GRID_LAYOUT_WIDTH
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc.BasicConnection
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc.Connection
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.webrtc.ConnectionCallbacks
import org.webrtc.EglBase
import org.webrtc.MediaStream
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoRenderer
import java.util.*

class Wamper(
    val activity: Activity,
    val roomId: WampRoomId = "abcdef"
) {

    private val userId = UUID.randomUUID().toString().substring(0, 8)

    // EglBase seems to handle all Surface related operations.
    private val eglBase = EglBase.create()

    private val connectionList: List<Connection> = emptyList()

    private var wamp: WampConnector? = null // is it necessary to be nullable ?

    fun findConnection(targetId: String): Connection? {
        var i = 0
        val max = connectionList.size
        while (i < max) {
            val connection = connectionList[i]
            if (connection.targetId().equals(targetId)) {
                return connection
            }
            i++
        }
        println("connection not found. $connectionList")
        return null
    }

    fun setup() {

        wamp = BasicWamp(
            activity, "roomId", "userId",
            object : WampCallbacks {
                override fun onOpen() {
                    wamp!!.publishCallme()
                }

                override fun onReceiveAnswer(targetId: WampTargetId, sdp: String) {
//                    val connection = findConnection(targetId)
                    findConnection(targetId)?.apply {
                        this.receiveAnswer(sdp)
                    } ?: println("connection not found onReceiveAnswer for targetId: $targetId")
                }

                override fun onReceiveOffer(targetId: WampTargetId, sdp: String) {
                    createConnection(targetId).apply {
                        receiveOffer(sdp)
                    }
                }

                override fun onIceCandidate(
                    targetId: WampTargetId,
                    candidate: String,
                    sdpMid: String,
                    sdpMLineIndex: Int
                ) {
                    val connection = createConnection(targetId)
                    connection.receiveCandidate(candidate, sdpMid, sdpMLineIndex)
                }

                override fun onReceiveCallme(targetId: WampTargetId) {
                    createConnection(targetId).apply {
                        this.publishOffer()
                    }
                }

                override fun onCloseConnection(targetId: WampTargetId) =
                    println("onCloseConnection called with targetId: $targetId")
            }
        )
    }

    private var remoteIndex = 0

    private fun createConnection(targetId: WampTargetId): Connection {
        val connection = BasicConnection(
            userId, targetId, wamp!!,
            object : ConnectionCallbacks {
                override fun onAddedStream(mediaStream: MediaStream) {
                    if (mediaStream.videoTracks.size == 0) {
                        println("no video tracks found. Bailing out ...")
                        return
                    }
                    val remoteVideoTrack = mediaStream.videoTracks.first

                    activity.runOnUiThread {
                        val remoteRenderer = SurfaceViewRenderer(activity)
                        val row = remoteIndex / 2
                        val col = remoteIndex % 2
//                    val params = GridLayout.LayoutParams().apply {
                        remoteRenderer.layoutParams = GridLayout.LayoutParams().apply {
                            columnSpec = GridLayout.spec(col, 1)
                            rowSpec = GridLayout.spec(row, 1)
                            width = GRID_LAYOUT_WIDTH
                            width = GRID_LAYOUT_HEIGHT
                            leftMargin = GRID_LAYOUT_LEFT_MARGIN
                            rightMargin = GRID_LAYOUT_RIGHT_MARGIN
                            topMargin = GRID_LAYOUT_TOP_MARGIN
                        }
//                    remoteRenderer.layoutParams = params

                        val videoRenderer = setupRenderer(remoteRenderer)
                        remoteVideoTrack.addRenderer(videoRenderer)
                    }
                }
            }
        )
    }

    private fun setupRenderer(remoteRenderer: SurfaceViewRenderer): VideoRenderer =
        with(remoteRenderer) {
            init(eglBase.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setZOrderMediaOverlay(true)
            setEnableHardwareScaler(true)
            VideoRenderer(this)
        }
}
