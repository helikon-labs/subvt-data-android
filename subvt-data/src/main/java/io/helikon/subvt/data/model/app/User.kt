package io.helikon.subvt.data.model.app

/**
 * A SubVT user defined by her public key (hex string).
 */
data class User(
    val id: Long,
    val publicKeyHex: String,
)