package io.helikon.subvt.data.model.rpc

data class RPCRequest(
    private val jsonrpc: String = "2.0",
    val id: Long,
    val method: String,
    val params: List<Any>,
)