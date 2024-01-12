package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.substrate.Era
import java.math.BigInteger

data class ValidatorEraPayoutReport(
    val era: Era,
    val payout: BigInteger,
)
