package io.helikon.subvt.data.service

import com.google.gson.reflect.TypeToken
import io.helikon.subvt.data.model.ValidatorDetails
import io.helikon.subvt.data.model.ValidatorDetailsDiff
import io.helikon.subvt.data.model.ValidatorDetailsUpdate
import io.helikon.subvt.data.model.rpc.RPCSubscriptionMessage

class ValidatorDetailsService(
    host: String,
    port: Int,
    private val listener: RPCSubscriptionListener<ValidatorDetails, ValidatorDetailsDiff>,
): RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>(
    host,
    port,
    listener,
    "subscribe_validatorDetails",
    "unsubscribe_validatorDetails"
) {

    private val responseType = TypeToken.getParameterized(
        RPCSubscriptionMessage::class.java,
        ValidatorDetailsUpdate::class.java,
    ).type

    override suspend fun processUpdate(json: String) {
        val update = gson.fromJson<RPCSubscriptionMessage<ValidatorDetailsUpdate>>(
            json,
            responseType,
        )
        if (update.params.body.validatorDetails != null) {
            listener.onSubscribed(
                this,
                subscriptionId,
                null,
                update.params.body.finalizedBlockNumber,
                update.params.body.validatorDetails,
            )
        } else {
            listener.onUpdateReceived(
                this,
                subscriptionId,
                null,
                update.params.body.finalizedBlockNumber,
                update.params.body.validatorDetailsUpdate,
            )
        }
    }

}