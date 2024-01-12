package io.helikon.subvt.data.model.substrate

import com.google.gson.annotations.SerializedName

enum class RewardDestinationType {
    @SerializedName("Account")
    ACCOUNT,

    @SerializedName("Controller")
    CONTROLLER,

    @SerializedName("None")
    NONE,

    @SerializedName("Staked")
    STAKED,

    @SerializedName("Stash")
    STASH,
}

/**
 * Reward destination for a staking account.
 */
data class RewardDestination(
    val destinationType: RewardDestinationType,
    val destination: AccountId?,
)
