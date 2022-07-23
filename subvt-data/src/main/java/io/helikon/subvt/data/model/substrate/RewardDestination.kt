package io.helikon.subvt.data.model.substrate

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import io.helikon.subvt.data.ss58.SS58Codec.hexToBytes
import java.lang.reflect.Type

/**
 * Reward destination for a staking account.
 */
sealed class RewardDestination {
    object Staked : RewardDestination()
    object Stash : RewardDestination()
    object Controller : RewardDestination()
    data class Account(val accountId: AccountId) : RewardDestination()
    object None : RewardDestination()
}

internal class RewardDestinationDeserializer : JsonDeserializer<RewardDestination> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): RewardDestination {
        return when (val destination = json.asString) {
            "Staked" -> RewardDestination.Staked
            "Stash" -> RewardDestination.Stash
            "Controller" -> RewardDestination.Controller
            "None" -> RewardDestination.None
            else -> RewardDestination.Account(
                AccountId(
                    destination
                        .removePrefix("Account(")
                        .removeSuffix(")")
                        .hexToBytes()
                )
            )
        }
    }

}