package io.helikon.subvt.data.model.substrate

/**
 * Validate intention parameters.
 */
data class ValidatorPreferences(
    val commissionPerBillion: Long,
    val blocksNominations: Boolean,
)
