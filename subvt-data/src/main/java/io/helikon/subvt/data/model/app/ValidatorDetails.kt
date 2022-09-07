package io.helikon.subvt.data.model.app

import io.helikon.subvt.data.model.substrate.*

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
)

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