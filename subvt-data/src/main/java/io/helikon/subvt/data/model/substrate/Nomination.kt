package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

/**
 * Nominator, stake an nominees.
 */
data class Nomination(
    val stashAccountId: AccountId,
    val submissionEraIndex: Int,
    val nomineeCount: Int,
    val stake: Stake,
)

data class InactiveNominationsSummary(
    val nominationCount: Int,
    val totalAmount: BigInteger,
)
