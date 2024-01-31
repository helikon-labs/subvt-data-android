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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.wss
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class RPCSubscriptionServiceStatus {
    data object Connected : RPCSubscriptionServiceStatus()

    data class Error(val error: Throwable?) : RPCSubscriptionServiceStatus()

    data object Idle : RPCSubscriptionServiceStatus()

    data class Subscribed(val subscriptionId: Long) : RPCSubscriptionServiceStatus()

    data object Unsubscribed : RPCSubscriptionServiceStatus()
}

/**
 * Abstract definitions for the RPC subscription services (network status, validator details,
 * active and inactive validator list).
 */
abstract class RPCSubscriptionService<K, T>(
    private val listener: RPCSubscriptionListener<K, T>,
    private var subscribeMethod: String,
    private var unsubscribeMethod: String,
) {
    private var rpcId: Long = 0
    private val _status =
        MutableStateFlow<RPCSubscriptionServiceStatus>(RPCSubscriptionServiceStatus.Idle)
    val status: StateFlow<RPCSubscriptionServiceStatus> = _status

    private val client: HttpClient =
        HttpClient {
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
    protected val gson: Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(AccountId::class.java, AccountIdDeserializer())
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
                            "Unsubscribed subscription id: $subscriptionId",
                        )
                        session = null
                        val oldSubscriptionId = subscriptionId
                        subscriptionId = 0
                        _status.value = RPCSubscriptionServiceStatus.Unsubscribed
                        listener.onUnsubscribed(this@RPCSubscriptionService, oldSubscriptionId)
                        break
                    } catch (error: Throwable) {
                        Logger.e("Unable to parse response JSON: $responseJSON")
                        session = null
                        subscriptionId = 0
                        _status.value = RPCSubscriptionServiceStatus.Error(error)
                        break
                    }
                }
            } catch (error: Throwable) {
                Logger.e("Error while receiving update: $error")
                session = null
                subscriptionId = 0
                _status.value = RPCSubscriptionServiceStatus.Error(error)
                break
            }
        }
    }

    suspend fun subscribe(
        host: String,
        port: Int,
        params: List<Any>,
    ) {
        if (session != null) {
            return
        }
        rpcId = (0..Int.MAX_VALUE).random().toLong()
        try {
            client.wss(host = host, port = port) {
                Logger.d("WebSockets session initialized.")
                _status.value = RPCSubscriptionServiceStatus.Connected
                try {
                    send(
                        gson.toJson(
                            RPCRequest(
                                id = rpcId,
                                method = subscribeMethod,
                                params = params,
                            ),
                        ),
                    )
                    val textFrame = readNextTextFrame(incoming)
                    val subscriptionStatus =
                        gson.fromJson(
                            textFrame.readText(),
                            RPCSubscribeStatus::class.java,
                        )
                    if (subscriptionStatus.subscriptionId <= 0) {
                        _status.value = RPCSubscriptionServiceStatus.Error(null)
                        throw SubscriptionException("Invalid subscription id: ${subscriptionStatus.subscriptionId}")
                    }
                    session = this
                    subscriptionId = subscriptionStatus.subscriptionId
                    Logger.d("Subscribed with id: $subscriptionId")
                    _status.value = RPCSubscriptionServiceStatus.Subscribed(subscriptionId)
                    beginIncomingProcessing(incoming)
                } catch (error: Throwable) {
                    _status.value = RPCSubscriptionServiceStatus.Error(error)
                }
            }
        } catch (error: Throwable) {
            session = null
            subscriptionId = 0
            _status.value = RPCSubscriptionServiceStatus.Error(error)
        }
    }

    suspend fun unsubscribe() {
        try {
            session?.send(
                gson.toJson(
                    RPCRequest(
                        id = rpcId,
                        method = unsubscribeMethod,
                        params = listOf(subscriptionId),
                    ),
                ),
            )
        } catch (error: Throwable) {
            session = null
            subscriptionId = 0
            _status.value = RPCSubscriptionServiceStatus.Error(error)
        }
    }

    abstract suspend fun processOnSubscribed(json: String)

    abstract suspend fun processUpdate(json: String)
}
