package io.helikon.subvt.data.model

import com.google.gson.annotations.SerializedName
import io.helikon.subvt.data.model.substrate.*

data class ValidatorSummary(
    val accountId: AccountId,
    val controllerAccountId: AccountId?,
    val display: String?,
    val parentDisplay: String?,
    val childDisplay: String?,
    val confirmed: Boolean,
    val preferences: ValidatorPreferences,
    val selfStake: StakeSummary,
    val isActive: Boolean,
    val activeNextSession: Boolean,
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
    val display: String?,
    val parentDisplay: String?,
    val childDisplay: String?,
    val confirmed: Boolean?,
    val preferences: ValidatorPreferences?,
    val selfStake: StakeSummary?,
    val isActive: Boolean?,
    val activeNextSession: Boolean?,
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

data class ValidatorListUpdate(
    val finalizedBlockNumber: Long?,
    val insert: List<ValidatorSummary>,
    val update: List<ValidatorSummaryDiff>,
    val removeIds: List<String>,
)