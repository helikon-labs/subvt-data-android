package io.helikon.subvt.data.model.substrate

/**
 * Substrate era as represented in the SubVT system.
 */
data class Era(
    val index: Int,
    val startTimestamp: Long,
    val endTimestamp: Long,
)