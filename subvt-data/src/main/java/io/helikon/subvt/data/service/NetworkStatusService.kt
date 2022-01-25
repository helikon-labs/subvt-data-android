package io.helikon.subvt.data.service

import com.google.gson.reflect.TypeToken
import io.helikon.subvt.data.model.NetworkStatus
import io.helikon.subvt.data.model.NetworkStatusDiff
import io.helikon.subvt.data.model.NetworkStatusUpdate
import io.helikon.subvt.data.model.rpc.RPCSubscriptionMessage

/**
 * Network status RPC service client.
 */
class NetworkStatusService(
    host: String,
    port: Int,
    private val listener: RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff>,
): RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>(
    host,
    port,
    listener,
    "subscribe_networkStatus",
    "unsubscribe_networkStatus"
) {
    private val responseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        NetworkStatusUpdate::class.java,
    ).type

    override suspend fun processOnSubscribed(json: String) {
        val update = gson.fromJson<RPCSubscriptionMessage<NetworkStatusUpdate>>(
            json,
            responseType,
        )
        listener.onSubscribed(
            this,
            subscriptionId,
            update.params.body.status?.bestBlockNumber,
            update.params.body.status?.finalizedBlockNumber,
            update.params.body.status!!,
        )
    }

    override suspend fun processUpdate(json: String) {
        val update = gson.fromJson<RPCSubscriptionMessage<NetworkStatusUpdate>>(
            json,
            responseType,
        )
        listener.onUpdateReceived(
            this,
            subscriptionId,
            update.params.body.diff?.bestBlockNumber,
            update.params.body.diff?.finalizedBlockNumber,
            update.params.body.diff,
        )
    }

}