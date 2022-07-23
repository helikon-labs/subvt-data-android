/**
 * Account id related classes and functions.
 */
package io.helikon.subvt.data.model.substrate

import com.google.gson.*
import io.helikon.subvt.data.ss58.SS58Codec.hexToBytes
import io.helikon.subvt.data.ss58.SS58Codec.toSS58
import java.lang.reflect.Type

private fun String.decodeAccountIdHex(): ByteArray {
    check(length % 2 == 0) { "Account id hex must have an even length." }
    val toDecode = if (startsWith("0x")) {
        substring(2)
    } else {
        this
    }
    val bytes = toDecode.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
    check(bytes.size == 32) { "Account id should be 32 bytes long." }
    return bytes
}

class AccountId(
    private val bytes: ByteArray
) {

    constructor(hex: String) : this(hex.decodeAccountIdHex())

    fun getAddress(prefix: Short): String = bytes.toSS58(prefix)

    fun getBytes(): ByteArray = bytes.clone()

    override fun toString() = "0x" + bytes.joinToString(separator = "") { eachByte ->
        "%02x".format(eachByte)
    }.uppercase()

    override fun equals(other: Any?) =
        (other is AccountId)
                && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}

internal class AccountIdDeserializer : JsonDeserializer<AccountId> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ) = AccountId(json.asString.hexToBytes())
}

internal class AccountIdSerializer: JsonSerializer<AccountId> {
    override fun serialize(
        accountId: AccountId,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = JsonPrimitive(accountId.toString())

}