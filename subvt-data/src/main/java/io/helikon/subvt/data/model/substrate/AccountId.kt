package io.helikon.subvt.data.model.substrate

import com.google.gson.*
import io.helikon.subvt.data.ss58.SS58Encoder.hexToBytes
import io.helikon.subvt.data.ss58.SS58Encoder.toSS58
import java.lang.reflect.Type

class AccountId(
    private val bytes: ByteArray
) {
    fun getAddress(prefix: Short): String = bytes.toSS58(prefix)

    fun getBytes(): ByteArray = bytes.clone()

    override fun toString() = "0x" + bytes.joinToString(separator = "") { eachByte ->
        "%02x".format(eachByte)
    }.uppercase()
}

internal class AccountIdDeserializer : JsonDeserializer<AccountId> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ) = AccountId(json.asString.hexToBytes())
}