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
) {
    fun apply(diff: ValidatorSummaryDiff) =
        ValidatorSummary(
            accountId = this.accountId,
            address = this.address,
            controllerAccountId = diff.controllerAccountId ?: this.controllerAccountId,
            networkId = diff.networkId ?: this.networkId,
            display = diff.display ?: this.display,
            parentDisplay = diff.parentDisplay ?: this.parentDisplay,
            childDisplay = diff.childDisplay ?: this.childDisplay,
            confirmed = diff.confirmed ?: this.confirmed,
            preferences = diff.preferences ?: this.preferences,
            selfStake = diff.selfStake ?: this.selfStake,
            isActive = diff.isActive ?: this.isActive,
            isActiveNextSession = diff.isActiveNextSession ?: this.isActiveNextSession,
            inactiveNominations = diff.inactiveNominations ?: this.inactiveNominations,
            oversubscribed = diff.oversubscribed ?: this.oversubscribed,
            slashCount = diff.slashCount ?: this.slashCount,
            isEnrolledIn1KV = diff.isEnrolledIn1KV ?: this.isEnrolledIn1KV,
            isParaValidator = diff.isParaValidator ?: this.isParaValidator,
            paraId = diff.paraId ?: this.paraId,
            returnRatePerBillion = diff.returnRatePerBillion ?: this.returnRatePerBillion,
            blocksAuthored = diff.blocksAuthored ?: this.blocksAuthored,
            rewardPoints = diff.rewardPoints ?: this.rewardPoints,
            heartbeatReceived = diff.heartbeatReceived ?: this.heartbeatReceived,
            validatorStake = diff.validatorStake ?: this.validatorStake,
        )
}

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
