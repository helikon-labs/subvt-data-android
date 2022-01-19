package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import io.helikon.subvt.data.exception.SubscriptionException
import io.helikon.subvt.data.model.*
import io.helikon.subvt.data.model.rpc.*
import io.helikon.subvt.data.model.rpc.RPCSubscribeStatus
import io.helikon.subvt.data.model.rpc.RPCSubscriptionMessage
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.AccountIdDeserializer
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*

class RPCService(
    private val host: String,
    private val port: Int
) {

    private val client: HttpClient = HttpClient {
        install(WebSockets) {
            pingInterval = 5000
        }
        install(HttpTimeout) {
            requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            connectTimeoutMillis = 10000
        }
    }
    private var sessionMap = mutableMapOf<Long, DefaultClientWebSocketSession>()
    private val networkStatusRPCResponseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        NetworkStatusUpdate::class.java,
    ).type
    private val validatorListRPCResponseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        ValidatorListUpdate::class.java,
    ).type
    private val validatorDetailsRPCResponseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        ValidatorDetailsUpdate::class.java,
    ).type

    companion object {
        private val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(AccountId::class.java, AccountIdDeserializer())
            .create()
    }

    suspend fun subscribeNetworkStatus(
        callback: suspend (Long, NetworkStatusUpdate) -> Unit
    ) {
        client.ws(host = host, port = port) {
            Logger.d("Network status WebSockets session initialized.")
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
            sessionMap[subscriptionStatus.subscriptionId] = this
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
                            networkStatusRPCResponseType,
                        )
                        callback(subscriptionStatus.subscriptionId, update.params.body)
                    } catch (ignored: Throwable) {
                        try {
                            gson.fromJson(
                                responseJSON,
                                RPCUnsubscribeStatus::class.java,
                            )
                            sessionMap.remove(subscriptionStatus.subscriptionId)
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

    suspend fun unsubscribeNetworkStatus(subscriptionId: Long) {
        sessionMap[subscriptionId]?.send(
            gson.toJson(
                RPCRequest(
                    id = 5,
                    method = "unsubscribe_networkStatus",
                    params = listOf(subscriptionId),
                )
            )
        )
    }

    suspend fun subscribeValidatorList(
        callback: suspend (Long, ValidatorListUpdate) -> Unit
    ) {
        client.ws(host = host, port = port) {
            Logger.d("Validator list WebSockets session initialized.")
            send(
                gson.toJson(
                    RPCRequest(
                        id = 5,
                        method = "subscribe_validatorList",
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
                throw SubscriptionException("Invalid validator list subscription id: ${subscriptionStatus.subscriptionId}")
            }
            sessionMap[subscriptionStatus.subscriptionId] = this
            Logger.d("Subscribed to validator list. Subscription id: ${subscriptionStatus.subscriptionId}")
            while (true) {
                try {
                    incomingFrame = incoming.receive()
                    textFrame = incomingFrame as? Frame.Text
                        ?: throw SubscriptionException("Cannot read incoming frame: $incomingFrame")
                    val responseJSON = textFrame.readText()
                    try {
                        val update = gson.fromJson<RPCSubscriptionMessage<ValidatorListUpdate>>(
                            responseJSON,
                            validatorListRPCResponseType,
                        )
                        callback(subscriptionStatus.subscriptionId, update.params.body)
                    } catch (ignored: Throwable) {
                        try {
                            gson.fromJson(
                                responseJSON,
                                RPCUnsubscribeStatus::class.java,
                            )
                            sessionMap.remove(subscriptionStatus.subscriptionId)
                            Logger.d(
                                "Unsubscribed from validator list. Subscription id: ${subscriptionStatus.subscriptionId}"
                            )
                            break
                        } catch (ignored: Throwable) {
                            Logger.e("Unable to parse validator list response JSON: $responseJSON")
                            break
                        }
                    }
                } catch (t: Throwable) {
                    Logger.e("Error while receiving validator list: $t")
                    break
                }
            }
        }
    }

    suspend fun unsubscribeValidatorList(subscriptionId: Long) {
        sessionMap[subscriptionId]?.send(
            gson.toJson(
                RPCRequest(
                    id = 5,
                    method = "unsubscribe_validatorList",
                    params = listOf(subscriptionId),
                )
            )
        )
    }

    suspend fun subscribeValidatorDetails(
        validatorAccountID: String,
        callback: suspend (Long, Long?, ValidatorDetails?, ValidatorDetailsDiff?) -> Unit
    ) {
        client.ws(host = host, port = port) {
            Logger.d("Validator details WebSockets session initialized.")
            send(
                gson.toJson(
                    RPCRequest(
                        id = 5,
                        method = "subscribe_validatorDetails",
                        params = listOf(validatorAccountID),
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
                throw SubscriptionException("Invalid validator details subscription id: ${subscriptionStatus.subscriptionId}")
            }
            sessionMap[subscriptionStatus.subscriptionId] = this
            Logger.d("Subscribed to validator details. Subscription id: ${subscriptionStatus.subscriptionId}")
            while (true) {
                try {
                    incomingFrame = incoming.receive()
                    textFrame = incomingFrame as? Frame.Text
                        ?: throw SubscriptionException("Cannot read incoming frame: $incomingFrame")
                    val responseJSON = textFrame.readText()
                    try {
                        val details = gson.fromJson<RPCSubscriptionMessage<ValidatorDetailsUpdate>>(
                            responseJSON,
                            validatorDetailsRPCResponseType,
                        )
                        callback(
                            subscriptionStatus.subscriptionId,
                            details.params.body.finalizedBlockNumber,
                            details.params.body.validatorDetails,
                            details.params.body.validatorDetailsUpdate,
                        )
                    } catch (ignored: Throwable) {
                        try {
                            gson.fromJson(
                                responseJSON,
                                RPCUnsubscribeStatus::class.java,
                            )
                            sessionMap.remove(subscriptionStatus.subscriptionId)
                            Logger.d(
                                "Unsubscribed from validator details. Subscription id: ${subscriptionStatus.subscriptionId}"
                            )
                            break
                        } catch (ignored: Throwable) {
                            Logger.e("Unable to parse validator details response JSON: $responseJSON")
                            break
                        }
                    }
                } catch (t: Throwable) {
                    Logger.e("Error while receiving validator details: $t")
                    break
                }
            }
        }
    }

    suspend fun unsubscribeValidatorDetails(subscriptionId: Long) {
        sessionMap[subscriptionId]?.send(
            gson.toJson(
                RPCRequest(
                    id = 5,
                    method = "unsubscribe_validatorDetails",
                    params = listOf(subscriptionId),
                )
            )
        )
    }

}