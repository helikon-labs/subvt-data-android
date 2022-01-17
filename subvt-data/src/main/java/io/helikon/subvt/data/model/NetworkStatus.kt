package io.helikon.subvt.data.model

import java.math.BigInteger

data class NetworkStatus(
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
    val diff: NetworkStatus?,
)