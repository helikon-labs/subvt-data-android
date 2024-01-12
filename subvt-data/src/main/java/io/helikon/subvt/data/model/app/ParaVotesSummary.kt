package io.helikon.subvt.data.model.app

/**
 * Number of explicit, implicit and missed votes of a para validator
 * over a session.
 */
data class ParaVotesSummary(
    val explicit: Int,
    val implicit: Int,
    val missed: Int,
)
