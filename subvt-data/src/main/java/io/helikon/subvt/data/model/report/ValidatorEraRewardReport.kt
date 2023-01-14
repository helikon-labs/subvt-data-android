package io.helikon.subvt.data.model.report

import io.helikon.subvt.data.model.substrate.Era
import java.math.BigInteger

data class ValidatorEraRewardReport(
    val era: Era,
    val reward: BigInteger,
)