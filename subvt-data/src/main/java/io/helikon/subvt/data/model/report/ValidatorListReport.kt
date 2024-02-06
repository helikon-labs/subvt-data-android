package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.model.substrate.BlockSummary
import kotlinx.collections.immutable.ImmutableList

data class ValidatorListReport(
    val finalizedBlock: BlockSummary,
    val validators: ImmutableList<ValidatorSummary>,
)
