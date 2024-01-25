package io.helikon.subvt.data.model.app

import io.helikon.subvt.data.model.substrate.Account
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.Nomination
import io.helikon.subvt.data.model.substrate.ParaCoreAssignment
import io.helikon.subvt.data.model.substrate.RewardDestination
import io.helikon.subvt.data.model.substrate.Stake
import io.helikon.subvt.data.model.substrate.ValidatorPreferences
import io.helikon.subvt.data.model.substrate.ValidatorStake

/**
 * Returned by the validator details service right after the initial subscription.
 */
data class ValidatorDetails(
    val account: Account,
    val controllerAccountId: AccountId,
    val networkId: Long,
    val preferences: ValidatorPreferences,
    val selfStake: Stake,
    val rewardDestination: RewardDestination,
    val nextSessionKeys: String,
    val isActive: Boolean,
    val isActiveNextSession: Boolean,
    val nominations: List<Nomination>,
    val oversubscribed: Boolean,
    val activeEraCount: Int,
    val inactiveEraCount: Int,
    val slashCount: Int,
    val offlineOffenceCount: Int,
    val unclaimedEraIndices: List<Int>,
    val isParaValidator: Boolean,
    val paraCoreAssignment: ParaCoreAssignment?,
    val returnRatePerBillion: Long?,
    val blocksAuthored: Int?,
    val rewardPoints: Long?,
    val heartbeatReceived: Boolean?,
    val validatorStake: ValidatorStake?,
    val onekvCandidateRecordId: Long?,
    val onekvRank: Int?,
    val onekvLocation: String?,
    val onekvIsValid: Boolean?,
    val onekvOfflineSince: Long?,
) {
    fun apply(diff: ValidatorDetailsDiff) =
        ValidatorDetails(
            account = this.account,
            controllerAccountId = diff.controllerAccountId ?: this.controllerAccountId,
            networkId = diff.networkId ?: this.networkId,
            preferences = diff.preferences ?: this.preferences,
            selfStake = diff.selfStake ?: this.selfStake,
            rewardDestination = diff.rewardDestination ?: this.rewardDestination,
            nextSessionKeys = diff.nextSessionKeys ?: this.nextSessionKeys,
            isActive = diff.isActive ?: this.isActive,
            isActiveNextSession = diff.isActiveNextSession ?: this.isActiveNextSession,
            nominations = diff.nominations ?: this.nominations,
            oversubscribed = diff.oversubscribed ?: this.oversubscribed,
            activeEraCount = diff.activeEraCount ?: this.activeEraCount,
            inactiveEraCount = diff.inactiveEraCount ?: this.inactiveEraCount,
            slashCount = diff.slashCount ?: this.slashCount,
            offlineOffenceCount = diff.offlineOffenceCount ?: this.offlineOffenceCount,
            unclaimedEraIndices = diff.unclaimedEraIndices ?: this.unclaimedEraIndices,
            isParaValidator = diff.isParaValidator ?: this.isParaValidator,
            paraCoreAssignment = diff.paraCoreAssignment ?: this.paraCoreAssignment,
            returnRatePerBillion = diff.returnRatePerBillion ?: this.returnRatePerBillion,
            blocksAuthored = diff.blocksAuthored ?: this.blocksAuthored,
            rewardPoints = diff.rewardPoints ?: this.rewardPoints,
            heartbeatReceived = diff.heartbeatReceived ?: this.heartbeatReceived,
            validatorStake = diff.validatorStake ?: this.validatorStake,
            onekvCandidateRecordId = diff.onekvCandidateRecordId ?: this.onekvCandidateRecordId,
            onekvRank = diff.onekvRank ?: this.onekvRank,
            onekvLocation = diff.onekvLocation ?: this.onekvLocation,
            onekvIsValid = diff.onekvIsValid ?: this.onekvIsValid,
            onekvOfflineSince = diff.onekvOfflineSince ?: this.onekvOfflineSince,
        )
}

/**
 * Subsequent data from the validator details service, reflecting the changes
 * to the previous state.
 */
data class ValidatorDetailsDiff(
    val account: Account,
    val controllerAccountId: AccountId?,
    val networkId: Long?,
    val preferences: ValidatorPreferences?,
    val selfStake: Stake?,
    val rewardDestination: RewardDestination?,
    val nextSessionKeys: String?,
    val isActive: Boolean?,
    val isActiveNextSession: Boolean?,
    val nominations: List<Nomination>?,
    val oversubscribed: Boolean?,
    val activeEraCount: Int?,
    val inactiveEraCount: Int?,
    val slashCount: Int?,
    val offlineOffenceCount: Int?,
    val unclaimedEraIndices: List<Int>?,
    val isParaValidator: Boolean?,
    val paraCoreAssignment: ParaCoreAssignment?,
    val returnRatePerBillion: Long?,
    val blocksAuthored: Int?,
    val rewardPoints: Long?,
    val heartbeatReceived: Boolean?,
    val validatorStake: ValidatorStake?,
    val onekvCandidateRecordId: Long?,
    val onekvRank: Int?,
    val onekvLocation: String?,
    val onekvIsValid: Boolean?,
    val onekvOfflineSince: Long?,
)

data class ValidatorDetailsUpdate(
    val finalizedBlockNumber: Long?,
    val validatorDetails: ValidatorDetails?,
    val validatorDetailsUpdate: ValidatorDetailsDiff?,
)
