package io.helikon.subvt.data.model.app

import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.InactiveNominationsSummary
import io.helikon.subvt.data.model.substrate.ValidatorStakeSummary

data class ValidatorSearchSummary(
    val accountId: AccountId,
    val address: String,
    val display: String?,
    val parentDisplay: String?,
    val childDisplay: String?,
    val confirmed: Boolean,
    val inactiveNominations: InactiveNominationsSummary,
    val validatorStake: ValidatorStakeSummary?,
)
