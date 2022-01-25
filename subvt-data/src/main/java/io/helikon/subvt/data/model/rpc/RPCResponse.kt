/**
 * Classes used in JSONRPC service communications.
 */
package io.helikon.subvt.data.model.rpc

import com.google.gson.annotations.SerializedName

internal data class RPCSubscribeStatus(
    private val jsonrpc: String,
    val id: Long,
    @SerializedName("result")
    val subscriptionId: Long,
)

internal data class RPCUnsubscribeStatus(
    private val jsonrpc: String,
    val id: Long,
    val result: String,
)

internal data class RPCSubscriptionMessage<T>(
    private val jsonrpc: String,
    val method: String,
    val params: RPCSubscriptionMessageParams<T>,
)

internal data class RPCSubscriptionMessageParams<T>(
    @SerializedName("subscription")
    val subscriptionId: Long,
    @SerializedName("result")
    val body: T,
)
