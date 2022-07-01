package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

import android.app.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WampFlags.HandshakeEndpoint
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.WampFlags.RealmOne
import org.json.JSONObject
import rx.android.app.AppObservable
import ws.wamp.jawampa.WampClient
import ws.wamp.jawampa.WampClientBuilder
import ws.wamp.jawampa.WampError
import java.util.concurrent.TimeUnit

class BasicWamp(
    val activity: Activity,
    val roomId: WampRoomId,
    val userId: WampUserId,
    val callbacks: WampCallbacks,
) : WampConnector {

    /**
     * topics are operations that the router can
     * connect to.
     */
    fun callMeTopic() = roomTopic(Topic.Callme.url)

    fun answerTopic(userId: WampUserId) =
        roomTopic(Topic.Answer.url.replace("[userId]", userId))

    fun closeTopic(userId: WampUserId /* = kotlin.String */) =
        roomTopic(Topic.Close.url)

    fun offerTopic(userId: WampUserId /* = kotlin.String */) =
        roomTopic(Topic.Offer.url.replace("[userId]", userId))

    fun candidateTopic(userId: WampUserId /* = kotlin.String */) =
        roomTopic(Topic.Candidate.url.replace("[userId]", userId))

    private lateinit var client: WampClient

    override fun connect() {
        println("Connect called - pls. install Timber! -- after connecting?! -- No, now!! - . - ")
        val builder = WampClientBuilder()
        try {
            client = builder
                .withUri(HandshakeEndpoint)
                .withRealm(RealmOne)
                .withInfiniteReconnects()
                .withReconnectInterval(3, TimeUnit.SECONDS)
                .build()
        } catch (e: WampError) {
            error(e.toString())
        }

        /* // Option A:
         // -> documentation @ https://github.com/Matthias247/jawampa
        client.statusChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                    println("connection WampClient.state: $it")

                },
                {
                    error("connection Error: $it")

                }
            )*/
        // Option B:
        AppObservable
            .bindFragment(this, client.statusChanged())
            .subscribe({ state ->
                when (state) {
                    is WampClient.ConnectedState -> onConnect()
                    is WampClient.ConnectingState -> onConnecting()
                    is WampClient.DisconnectedState -> onDisconnected()
                }
            }, { error ->
                error(error)
            })

        client.open()
    }

    override fun publishCallme() {
        val callmeTopic = callMeTopic()
        val mapper = ObjectMapper()
        val args = mapper.createObjectNode()
        args.put("targetId", userId)
        println("publishCallme - callmeTopic: $callmeTopic, targetId: $userId")
        client.publish(callmeTopic, userId)
    }

    override fun publishOffer(targetId: WampTargetId, sdp: String) {
        val topic = offerTopic(targetId)
        client.publish(topic, userId, sdp)
    }

    override fun publishAnswer(targetId: WampTargetId, sdp: String) {
        val topic = answerTopic(targetId)
        client.publish(topic, userId, sdp)
    }

    override fun publishCandidate(targetId: WampTargetId, candidate: String) {
        val topic = candidateTopic(targetId)
        client.publish(topic, userId, candidate)
    }

    // ** callback implementations **

    private fun onConnect() {
        // handle Offer
        val offerTopic = offerTopic(userId)
        // response is the payload received from subscription
        makeOfferSubscription(offerTopic)

        // handle candidate
        val candidateTopic = candidateTopic(userId)
        // response is the payload received from subscription
        makeCandidateSubscription(candidateTopic)

        // handle callme
        val callmeTopic = callMeTopic()
        // response is the payload received from subscription
        makeCallmeSubscription(callmeTopic)

        // handle close
        val closeTopic = closeTopic(userId)
        // response is the payload received from subscription
        makeCloseSubscription(callmeTopic)

        // handle onOpen
        callbacks.onOpen()
    }

    private fun makeCallmeSubscription(callmeTopic: String) =
        client.makeSubscription(callmeTopic).subscribe({ pubSubData ->
            val arguments = pubSubData.arguments()
            val targetId = arguments.get(0).asText()

            if (targetId == userId) {
                println("call yourself ?")
                return@subscribe
            }
            callbacks.onReceiveCallme(targetId)
        }, { error -> error(error) })

    private fun makeCloseSubscription(closeTopic: String) =
        client.makeSubscription(closeTopic).subscribe({ pubSubData ->
            val arguments = pubSubData.arguments()
            val targetId = arguments.get(0).asText()

            callbacks.onCloseConnection(targetId)
        }, { error -> error(error) })

    fun makeCandidateSubscription(candidateTopic: String) =
        client.makeSubscription(candidateTopic).subscribe({ pubSubData ->
            val targetId = pubSubData.arguments().get(0).asText()
            val jsonString = pubSubData.arguments().get(1).asText()
            try {
                val json = JSONObject(jsonString)
                var sdp: String? = null
                if (!json.has("candidate")) {
                    return@subscribe
                }
                sdp = json.getString("candidate")
                val sdpMid = json.getString("sdpMid")
                val sdpMLineIndex = json.getInt("sdpMLineIndex")

                callbacks.onIceCandidate(targetId, sdp, sdpMid, sdpMLineIndex)
            } catch (e: WampError) {
                error(e)
            }
        }, { error -> error(error) })

    fun makeOfferSubscription(offerTopic: String) =
        client.makeSubscription(offerTopic).subscribe({ response ->
            val arguments = response.arguments()
            val targetId = arguments.get(0).asText()

            val node = arguments.get(1)

            val sdpString = node.asText()

            try {
                val obj = JSONObject(sdpString)
                val s = obj.getString("sdp")
                callbacks.onReceiveOffer(targetId, s)
            } catch (e: WampError) {
                error(e)
            }
        }, { error -> error(error) })

    private fun onConnecting() {
    }

    private fun onDisconnected() {
    }

    // ** helper methods **

    private fun roomTopic(base: String) =
        base.replace("[roomId]", roomId)
}
