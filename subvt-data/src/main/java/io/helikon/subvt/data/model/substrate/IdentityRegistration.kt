package io.helikon.subvt.data.model.substrate

/**
 * Account identity (can be non-existent for an account).
 */
data class IdentityRegistration(
    val display: String?,
    val email: String?,
    val riot: String?,
    val twitter: String?,
    val web: String?,
    val confirmed: Boolean,
)