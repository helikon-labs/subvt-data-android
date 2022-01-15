package io.helikon.subvt.data.model

data class Epoch(
    val index: Int,
    val startBlockNumber: Long,
    val startTimestamp: Long,
    val endTimestamp: Long,
)