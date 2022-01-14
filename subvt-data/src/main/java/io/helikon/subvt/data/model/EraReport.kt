package io.helikon.subvt.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class EraReport(
    @SerializedName("era")
    val era: Era,
    @SerializedName("minimum_stake")
    val minimumStake: BigInteger?,
    @SerializedName("maximum_stake")
    val maximumStake: BigInteger?,
    @SerializedName("average_stake")
    val averageStake: BigInteger?,
    @SerializedName("median_stake")
    val medianStake: BigInteger?,
    @SerializedName("total_validator_reward")
    val totalValidatorReward: BigInteger?,
    @SerializedName("total_reward_points")
    val totalRewardPoints: Long?,
    @SerializedName("total_reward")
    val totalReward: BigInteger,
    @SerializedName("total_stake")
    val totalStake: BigInteger?,
    @SerializedName("active_nominator_count")
    val activeNominatorCount: Int?,
    @SerializedName("offline_offence_count")
    val offlineOffenceCount: Int?,
    @SerializedName("slashed_amount")
    val slashedAmount: BigInteger,
    @SerializedName("chilling_count")
    val chillingCount: Int,
)