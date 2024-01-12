package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

/**
 * Era staker account id, total staked amount and active amount.
 */
data class Stake(
    val stashAccountId: AccountId,
    val totalAmount: BigInteger,
    val activeAmount: BigInteger,
)

data class StakeSummary(
    val stashAccountId: String,
    val activeAmount: BigInteger,
)
