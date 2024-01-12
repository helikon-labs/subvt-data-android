package io.helikon.subvt.data.service

import com.google.gson.reflect.TypeToken
import io.helikon.subvt.data.model.app.ValidatorListUpdate
import io.helikon.subvt.data.model.rpc.RPCPublishedMessage

/**
 * Validator list RPC service client.
 */
class ValidatorListService(
    host: String,
    port: Int,
    private val listener: RPCSubscriptionListener<ValidatorListUpdate, ValidatorListUpdate>,
) : RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>(
        host,
        port,
        listener,
        "subscribe_validatorList",
        "unsubscribe_validatorList",
    ) {
    private val responseType =
        TypeToken.getParameterized(
            RPCPublishedMessage::class.java,
            ValidatorListUpdate::class.java,
        ).type

    override suspend fun processOnSubscribed(json: String) {
        val update =
            gson.fromJson<RPCPublishedMessage<ValidatorListUpdate>>(
                json,
                responseType,
            )
        listener.onSubscribed(
            this,
            subscriptionId,
            null,
            update.params.body.finalizedBlockNumber,
            update.params.body,
        )
    }

    override suspend fun processUpdate(json: String) {
        val update =
            gson.fromJson<RPCPublishedMessage<ValidatorListUpdate>>(
                json,
                responseType,
            )
        listener.onUpdateReceived(
            this,
            subscriptionId,
            null,
            update.params.body.finalizedBlockNumber,
            update.params.body,
        )
    }
}
