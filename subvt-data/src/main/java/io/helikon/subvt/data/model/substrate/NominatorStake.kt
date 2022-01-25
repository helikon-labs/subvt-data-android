package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

/**
 * Account and total stake.
 */
data class NominatorStake(
    val account: Account,
    val stake: BigInteger,
)