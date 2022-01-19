package io.helikon.subvt.data.model.substrate

import java.math.BigInteger

data class Nomination(
    val stashAccountId: AccountId,
    val submissionEraIndex: Int,
    val targetAccountIds: List<String>,
    val stake: Stake,
)

data class InactiveNominationsSummary(
    val nominationCount: Int,
    val totalAmount: BigInteger,
)