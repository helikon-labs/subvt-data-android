package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

/**
 * Complete staking information for an active validator account.
 */
data class ValidatorStake(
    val account: Account,
    val selfStake: BigInteger,
    val totalStake: BigInteger,
    val nominators: List<NominatorStake>,
)

data class ValidatorStakeSummary(
    val selfStake: BigInteger,
    val totalStake: BigInteger,
    val nominatorCount: Int,
)
