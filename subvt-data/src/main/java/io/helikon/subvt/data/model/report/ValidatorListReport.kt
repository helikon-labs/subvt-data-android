package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.model.substrate.BlockSummary

data class ValidatorListReport(
    val finalizedBlock: BlockSummary,
    val validators: List<ValidatorSummary>,
)
