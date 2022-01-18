package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

data class NominatorStake(
    val account: Account,
    val stake: BigInteger,
)