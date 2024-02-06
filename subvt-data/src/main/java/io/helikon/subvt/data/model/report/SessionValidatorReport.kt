package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.app.ParaVotesSummary
import io.helikon.subvt.data.model.substrate.BlockSummary
import io.helikon.subvt.data.model.substrate.Epoch
import io.helikon.subvt.data.model.substrate.HeartbeatEvent
import kotlinx.collections.immutable.ImmutableList

/**
 * Report of a validator's activity over a session/epoch.
 */
data class SessionValidatorReport(
    val session: Epoch,
    val isActive: Boolean,
    val validatorIndex: Long?,
    val heartbeatEvent: HeartbeatEvent?,
    val blocksAuthored: ImmutableList<BlockSummary>?,
    val paraValidatorGroupIndex: Long?,
    val paraValidatorIndex: Long?,
    val paraVotesSummary: ParaVotesSummary?,
)
