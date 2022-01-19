package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

data class Stake(
    val stashAccountId: AccountId,
    val totalAmount: BigInteger,
    val activeAmount: BigInteger,
)

data class StakeSummary(
    val stashAccountId: String,
    val activeAmount: BigInteger,
)