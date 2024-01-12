package io.helikon.subvt.data.model.substrate

/**
 * Substrate epoch as represented in the SubVT system.
 */
data class Epoch(
    val index: Int,
    val startBlockNumber: Long,
    val startTimestamp: Long,
    val endTimestamp: Long,
)
