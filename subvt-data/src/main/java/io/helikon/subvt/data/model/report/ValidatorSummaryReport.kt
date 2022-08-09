package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.app.ValidatorSummary
import io.helikon.subvt.data.model.substrate.BlockSummary

data class ValidatorSummaryReport(
    val finalizedBlock: BlockSummary,
    val validatorSummary: ValidatorSummary,
)
