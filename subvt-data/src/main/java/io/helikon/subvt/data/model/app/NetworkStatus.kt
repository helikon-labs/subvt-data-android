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
)

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