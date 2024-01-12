package io.helikon.subvt.data.model.substrate

/**
 * A Substrate account as represented in the SubVT system.
 */
data class Account(
    val id: AccountId,
    val address: String,
    val identity: IdentityRegistration?,
    val parent: Account?,
    val childDisplay: String?,
    val discoveredAt: Long?,
    val killedAt: Long?,
)
