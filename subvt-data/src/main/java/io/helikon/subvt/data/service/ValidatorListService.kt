package io.helikon.subvt.data.service

import com.google.gson.reflect.TypeToken
import io.helikon.subvt.data.model.ValidatorListUpdate
import io.helikon.subvt.data.model.rpc.RPCSubscriptionMessage

class ValidatorListService(
    host: String,
    port: Int,
    private val listener: RPCSubscriptionListener<ValidatorListUpdate, ValidatorListUpdate>
): RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>(
    host,
    port,
    listener,
    "subscribe_validatorList",
    "unsubscribe_validatorList"
) {

    private var isFirstUpdate = true
    private val responseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        ValidatorListUpdate::class.java,
    ).type

    override suspend fun processUpdate(json: String) {
        val update = gson.fromJson<RPCSubscriptionMessage<ValidatorListUpdate>>(
            json,
            responseType,
        )
        if (isFirstUpdate) {
            listener.onSubscribed(
                this,
                subscriptionId,
                null,
                update.params.body.finalizedBlockNumber,
                update.params.body,
            )
            isFirstUpdate = false
        } else {
            listener.onUpdateReceived(
                this,
                subscriptionId,
                null,
                update.params.body.finalizedBlockNumber,
                update.params.body,
            )
        }
    }

}