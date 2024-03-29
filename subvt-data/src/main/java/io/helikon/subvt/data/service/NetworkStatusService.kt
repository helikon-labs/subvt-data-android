package io.helikon.subvt.data.service

import com.google.gson.reflect.TypeToken
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.app.NetworkStatusDiff
import io.helikon.subvt.data.model.app.NetworkStatusUpdate
import io.helikon.subvt.data.model.rpc.RPCPublishedMessage

/**
 * Network status RPC service client.
 */
class NetworkStatusService(
    private val listener: RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff>,
) : RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>(
        listener,
        "subscribe_networkStatus",
        "unsubscribe_networkStatus",
    ) {
    private val responseType =
        TypeToken.getParameterized(
            RPCPublishedMessage::class.java,
            NetworkStatusUpdate::class.java,
        ).type

    override suspend fun processOnSubscribed(json: String) {
        val update =
            gson.fromJson<RPCPublishedMessage<NetworkStatusUpdate>>(
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
        val update =
            gson.fromJson<RPCPublishedMessage<NetworkStatusUpdate>>(
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
