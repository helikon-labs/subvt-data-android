package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.InactiveNominationsSummary
import io.helikon.subvt.data.model.substrate.StakeSummary
import io.helikon.subvt.data.model.substrate.ValidatorPreferences
import io.helikon.subvt.data.model.substrate.ValidatorStakeSummary

/**
 * A validator represented by the active or inactive validator details service.
 */
data class ValidatorSummary(
    val accountId: AccountId,
    val address: String,
    val controllerAccountId: AccountId?,
    val networkId: Long,
    val display: String?,
    val parentDisplay: String?,
    val childDisplay: String?,
    val confirmed: Boolean,
    val preferences: ValidatorPreferences,
    val selfStake: StakeSummary,
    val isActive: Boolean,
    val isActiveNextSession: Boolean,
    val inactiveNominations: InactiveNominationsSummary,
    val oversubscribed: Boolean,
    val slashCount: Int,
    @SerializedName("is_enrolled_in_1kv")
    val isEnrolledIn1KV: Boolean,
    val isParaValidator: Boolean,
    val paraId: Int?,
    val returnRatePerBillion: Long?,
    val blocksAuthored: Int?,
    val rewardPoints: Long?,
    val heartbeatReceived: Boolean?,
    val validatorStake: ValidatorStakeSummary?,
)

data class ValidatorSummaryDiff(
    val accountId: AccountId,
    val controllerAccountId: AccountId?,
    val networkId: Long?,
    val display: String?,
    val parentDisplay: String?,
    val childDisplay: String?,
    val confirmed: Boolean?,
    val preferences: ValidatorPreferences?,
    val selfStake: StakeSummary?,
    val isActive: Boolean?,
    val isActiveNextSession: Boolean?,
    val inactiveNominations: InactiveNominationsSummary?,
    val oversubscribed: Boolean?,
    val slashCount: Int?,
    @SerializedName("is_enrolled_in_1kv")
    val isEnrolledIn1KV: Boolean?,
    val isParaValidator: Boolean?,
    val paraId: Int?,
    val returnRatePerBillion: Long?,
    val blocksAuthored: Int?,
    val rewardPoints: Long?,
    val heartbeatReceived: Boolean?,
    val validatorStake: ValidatorStakeSummary?,
)

/**
 * Validator list service response - contains new, changes and removed validator info.
 * Will only contain inserts in the first response.
 */
data class ValidatorListUpdate(
    val finalizedBlockNumber: Long?,
    val insert: List<ValidatorSummary>,
    val update: List<ValidatorSummaryDiff>,
    val removeIds: List<String>,
)
