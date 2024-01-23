package io.helikon.subvt.data.model.app

import io.helikon.subvt.data.model.substrate.Epoch
import io.helikon.subvt.data.model.substrate.Era
import java.math.BigInteger

/**
 * Returned by the network status service right after the initial subscription.
 */
data class NetworkStatus(
    val finalizedBlockNumber: Long,
    val finalizedBlockHash: String,
    val bestBlockNumber: Long,
    val bestBlockHash: String,
    val activeEra: Era,
    val currentEpoch: Epoch,
    val activeValidatorCount: Int,
    val inactiveValidatorCount: Int,
    val lastEraTotalReward: BigInteger,
    val totalStake: BigInteger,
    val returnRatePerMillion: Int,
    val minStake: BigInteger,
    val maxStake: BigInteger,
    val averageStake: BigInteger,
    val medianStake: BigInteger,
    val eraRewardPoints: Long,
) {
    fun apply(diff: NetworkStatusDiff) =
        NetworkStatus(
            finalizedBlockNumber = diff.finalizedBlockNumber ?: this.finalizedBlockNumber,
            finalizedBlockHash = diff.finalizedBlockHash ?: this.finalizedBlockHash,
            bestBlockNumber = diff.bestBlockNumber ?: this.bestBlockNumber,
            bestBlockHash = diff.bestBlockHash ?: this.bestBlockHash,
            activeEra = diff.activeEra ?: this.activeEra,
            currentEpoch = diff.currentEpoch ?: this.currentEpoch,
            activeValidatorCount = diff.activeValidatorCount ?: this.activeValidatorCount,
            inactiveValidatorCount = diff.inactiveValidatorCount ?: this.inactiveValidatorCount,
            lastEraTotalReward = diff.lastEraTotalReward ?: this.lastEraTotalReward,
            totalStake = diff.totalStake ?: this.totalStake,
            returnRatePerMillion = diff.returnRatePerMillion ?: this.returnRatePerMillion,
            minStake = diff.minStake ?: this.minStake,
            maxStake = diff.maxStake ?: this.maxStake,
            averageStake = diff.averageStake ?: this.averageStake,
            medianStake = diff.medianStake ?: this.medianStake,
            eraRewardPoints = diff.eraRewardPoints ?: this.eraRewardPoints,
        )
}

/**
 * Subsequent data from the network status service, reflecting the changes
 * to the previous state.
 */
data class NetworkStatusDiff(
    val finalizedBlockNumber: Long?,
    val finalizedBlockHash: String?,
    val bestBlockNumber: Long?,
    val bestBlockHash: String?,
    val activeEra: Era?,
    val currentEpoch: Epoch?,
    val activeValidatorCount: Int?,
    val inactiveValidatorCount: Int?,
    val lastEraTotalReward: BigInteger?,
    val totalStake: BigInteger?,
    val returnRatePerMillion: Int?,
    val minStake: BigInteger?,
    val maxStake: BigInteger?,
    val averageStake: BigInteger?,
    val medianStake: BigInteger?,
    val eraRewardPoints: Long?,
)

data class NetworkStatusUpdate(
    val network: String,
    val status: NetworkStatus?,
    val diff: NetworkStatusDiff?,
)
