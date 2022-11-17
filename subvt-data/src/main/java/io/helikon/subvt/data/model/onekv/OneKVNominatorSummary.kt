package io.helikon.subvt.data.model.onekv

import io.helikon.subvt.data.model.substrate.AccountId
import java.math.BigInteger

/**
 * 1KV nominator summary.
 */
data class OneKVNominatorSummary(
    val id: Long,
    val onekvId: String,
    val stashAccountId: AccountId,
    val stashAddress: String,
    val bondedAmount: BigInteger,
    val lastNominationAt: Long,
)