package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.model.substrate.BlockSummary

data class ValidatorDetailsReport(
    val finalizedBlock: BlockSummary,
    val validatorDetails: ValidatorDetails,
)
