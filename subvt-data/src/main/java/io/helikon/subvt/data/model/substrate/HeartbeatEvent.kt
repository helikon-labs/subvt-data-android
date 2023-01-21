package io.helikon.subvt.data.model.substrate

/**
 * Substrate heartbeat event.
 */
data class HeartbeatEvent(
    val block: BlockSummary,
    val eventIndex: Int,
    val imOnlineKey: String,
)
