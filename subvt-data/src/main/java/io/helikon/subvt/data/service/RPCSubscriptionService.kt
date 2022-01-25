package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import io.helikon.subvt.data.exception.SubscriptionException
import io.helikon.subvt.data.model.rpc.RPCRequest
import io.helikon.subvt.data.model.rpc.RPCSubscribeStatus
import io.helikon.subvt.data.model.rpc.RPCUnsubscribeStatus
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.AccountIdDeserializer
import io.helikon.subvt.data.model.substrate.RewardDestination
import io.helikon.subvt.data.model.substrate.RewardDestinationDeserializer
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel

abstract class RPCSubscriptionService<K, T>(
    private val host: String,
    private val port: Int,
    private val listener: RPCSubscriptionListener<K, T>,
    private var subscribeMethod: String,
    private var unsubscribeMethod: String,
) {
    private var rpcId: Long = 0

    private val client: HttpClient = HttpClient {
        install(WebSockets) {
            pingInterval = 10 * 1000
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 3 * 60 * 1000
            connectTimeoutMillis = 1 * 60 * 1000
        }
    }
    private var session: DefaultClientWebSocketSession? = null
    protected var subscriptionId: Long = 0
    protected val gson: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(AccountId::class.java, AccountIdDeserializer())
        .registerTypeAdapter(RewardDestination::class.java, RewardDestinationDeserializer())
        .create()

    private suspend fun readNextTextFrame(incoming: ReceiveChannel<Frame>): Frame.Text {
        val incomingFrame = incoming.receive()
        return incomingFrame as? Frame.Text
            ?: throw SubscriptionException("Cannot read incoming frame: $incomingFrame")
    }

    private suspend fun beginIncomingProcessing(incoming: ReceiveChannel<Frame>) {
        var isFirstResponse = true
        while (true) {
            try {
                val responseJSON = readNextTextFrame(incoming).readText()
                try {
                    if (isFirstResponse) {
                        processOnSubscribed(responseJSON)
                        isFirstResponse = false
                    } else {
                        processUpdate(responseJSON)
                    }
                } catch (ignored: Throwable) {
                    try {
                        gson.fromJson(
                            responseJSON,
                            RPCUnsubscribeStatus::class.java,
                        )
                        Logger.d(
                            "Unsubscribed subscription id: $subscriptionId"
                        )
                        listener.onUnsubscribed(this@RPCSubscriptionService, subscriptionId)
                        session = null
                        subscriptionId = 0
                        break
                    } catch (ignored: Throwable) {
                        Logger.e("Unable to parse response JSON: $responseJSON")
                        break
                    }
                }
            } catch (t: Throwable) {
                Logger.e("Error while receiving update: $t")
                break
            }
        }
    }

    suspend fun subscribe(params: List<Any>) {
        if (session != null) {
            return
        }
        rpcId = (0..Int.MAX_VALUE).random().toLong()
        client.ws(host = host, port = port) {
            Logger.d("WebSockets session initialized.")
            send(
                gson.toJson(
                    RPCRequest(
                        id = rpcId,
                        method = subscribeMethod,
                        params = params,
                    )
                )
            )
            val textFrame = readNextTextFrame(incoming)
            val subscriptionStatus = gson.fromJson(
                textFrame.readText(),
                RPCSubscribeStatus::class.java,
            )
            if (subscriptionStatus.subscriptionId <= 0) {
                throw SubscriptionException("Invalid subscription id: ${subscriptionStatus.subscriptionId}")
            }
            session = this
            subscriptionId = subscriptionStatus.subscriptionId
            Logger.d("Subscribed with id: $subscriptionId")
            beginIncomingProcessing(incoming)
        }
    }

    suspend fun unsubscribe() {
        session?.send(
            gson.toJson(
                RPCRequest(
                    id = rpcId,
                    method = unsubscribeMethod,
                    params = listOf(subscriptionId),
                )
            )
        )
    }

    abstract suspend fun processOnSubscribed(json: String)
    abstract suspend fun processUpdate(json: String)
}