package io.helikon.subvt.data.model.substrate

data class ValidatorPreferences(
    val commissionPerBillion: Long,
    val blocksNominations: Boolean,
)