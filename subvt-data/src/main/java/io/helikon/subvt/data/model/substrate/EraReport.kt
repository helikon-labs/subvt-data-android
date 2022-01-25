package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

/**
 * Report for a single era.
 */
data class EraReport(
    val era: Era,
    val minimumStake: BigInteger?,
    val maximumStake: BigInteger?,
    val averageStake: BigInteger?,
    val medianStake: BigInteger?,
    val totalValidatorReward: BigInteger?,
    val totalRewardPoints: Long?,
    val totalReward: BigInteger,
    val totalStake: BigInteger?,
    val activeNominatorCount: Int?,
    val offlineOffenceCount: Int?,
    val slashedAmount: BigInteger,
    val chillingCount: Int,
)