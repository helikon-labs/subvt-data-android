package io.helikon.subvt.data.model.rpc

/**
 * Used in JSONRPC service communications.
 */
internal data class RPCRequest(
    private val jsonrpc: String = "2.0",
    val id: Long,
    val method: String,
    val params: List<Any>,
)
