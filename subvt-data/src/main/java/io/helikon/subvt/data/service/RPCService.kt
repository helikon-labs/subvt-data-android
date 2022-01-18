package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import io.helikon.subvt.data.exception.SubscriptionException
import io.helikon.subvt.data.model.NetworkStatusUpdate
import io.helikon.subvt.data.model.rpc.*
import io.helikon.subvt.data.model.rpc.RPCSubscribeStatus
import io.helikon.subvt.data.model.rpc.RPCSubscriptionMessage
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*

class RPCService(
    private val host: String,
    private val port: Int
) {

    private val client: HttpClient = HttpClient {
        install(WebSockets)
    }
    private var session: DefaultClientWebSocketSession? = null
    private val rpcResponseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        NetworkStatusUpdate::class.java,
    ).type

    companion object {
        private val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    suspend fun unsubscribeNetworkStatus(subscriptionId: Long) {
        session?.send(
            gson.toJson(
                RPCRequest(
                    id = 5,
                    method = "unsubscribe_networkStatus",
                    params = listOf(subscriptionId),
                )
            )
        )
    }

    suspend fun subscribeNetworkStatus(
        callback: suspend (Long, NetworkStatusUpdate) -> Unit
    ) {
        client.ws(host = host, port = port) {
            session = this
            Logger.d("WebSockets session initialized.")
            send(
                gson.toJson(
                    RPCRequest(
                        id = 5,
                        method = "subscribe_networkStatus",
                        params = listOf(),
                    )
                )
            )
            var incomingFrame = incoming.receive()
            var textFrame = incomingFrame as? Frame.Text
                ?: throw SubscriptionException("Cannot read incoming frame: $incomingFrame")
            val subscriptionStatus = gson.fromJson(
                textFrame.readText(),
                RPCSubscribeStatus::class.java,
            )
            if (subscriptionStatus.subscriptionId <= 0) {
                throw SubscriptionException("Invalid subscription id: ${subscriptionStatus.subscriptionId}")
            }
            Logger.d("Subscribed to network status. Subscription id: ${subscriptionStatus.subscriptionId}")
            while (true) {
                try {
                    incomingFrame = incoming.receive()
                    textFrame = incomingFrame as? Frame.Text
                        ?: throw SubscriptionException("Cannot read incoming frame: $incomingFrame")
                    val responseJSON = textFrame.readText()
                    try {
                        val update = gson.fromJson<RPCSubscriptionMessage<NetworkStatusUpdate>>(
                            responseJSON,
                            rpcResponseType,
                        )
                        callback(subscriptionStatus.subscriptionId, update.params.body)
                    } catch (ignored: Throwable) {
                        try {
                            gson.fromJson(
                                responseJSON,
                                RPCUnsubscribeStatus::class.java,
                            )
                            Logger.d(
                                "Unsubscribed from network status. Subscription id: ${subscriptionStatus.subscriptionId}"
                            )
                            break
                        } catch (ignored: Throwable) {
                            Logger.e("Unable to parse response JSON: $responseJSON")
                            break
                        }
                    }
                } catch (t: Throwable) {
                    Logger.e("Error while receiving network status: $t")
                    break
                }
            }
        }
    }
}