package de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.wamp

import android.app.Activity
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.Wamp.HandshakeEndpoint
import de.adesso.mobile.euriale5gersthelfer.e5gwebrtc.Wamp.RealmOne
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
    fun callMeTopic(userId: WampUserId /* = kotlin.String */) =
        roomTopic(Topic.Callme.url)

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
    }

    override fun publishCallme() {
        TODO("Not yet implemented")
    }

    override fun publishOffer(targetId: WampTargetId, sdp: String) {
        TODO("Not yet implemented")
    }

    override fun publishAnswer(targetId: WampTargetId, sdp: String) {
        TODO("Not yet implemented")
    }

    override fun publishCandidate(targetId: WampTargetId, candidate: String) {
        TODO("Not yet implemented")
    }

    // ** callback implementations **

    private fun onConnect() {
    }

    private fun onConnecting() {
    }

    private fun onDisconnected() {
    }

    // ** helper methods **

    private fun roomTopic(base: String) =
        base.replace("[roomId]", roomId)
}
