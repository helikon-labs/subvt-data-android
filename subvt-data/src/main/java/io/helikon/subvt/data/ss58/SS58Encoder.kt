package io.helikon.subvt.data.ss58

import io.helikon.subvt.data.encrypt.Base58
import io.helikon.subvt.data.encrypt.json.copyBytes
import io.helikon.subvt.data.exception.AddressFormatException
import io.helikon.subvt.data.hash.Hasher.blake2b256
import io.helikon.subvt.data.hash.Hasher.blake2b512
import java.lang.Exception
import kotlin.experimental.and
import kotlin.experimental.or

object SS58Encoder {
    private val PREFIX = "SS58PRE".toByteArray(Charsets.UTF_8)
    private const val PREFIX_SIZE = 2
    private const val PUBLIC_KEY_SIZE = 32

    private val base58 = Base58()

    private fun getPrefixLenIdent(decodedByteArray: ByteArray): Pair<Int, Short> {
        return when {
            decodedByteArray[0] in 0..63 -> 1 to decodedByteArray[0].toShort()
            decodedByteArray[0] in 64..16383 -> {
                val lower =
                    (((decodedByteArray[0] and 0b00111111).toInt() shl 2) or (decodedByteArray[1].toUByte()
                        .toInt() shr 6)).toShort()
                val upper = (decodedByteArray[1] and 0b00111111)
                2 to (lower or (upper.toInt() shl 8).toShort())
            }
            else -> throw IllegalArgumentException("Incorrect address byte")
        }
    }

    private fun encode(publicKey: ByteArray, addressByte: Short): String {
        val normalizedKey = if (publicKey.size > 32) {
            publicKey.blake2b256()
        } else {
            publicKey
        }
        val addressTypeByteArray = when (val ident = addressByte and 0b00111111_11111111) {
            in 0..63 -> byteArrayOf(ident.toByte())
            in 64..16383 -> {
                val first = (ident and 0b00000000_11111100).toInt() shr 2
                val second =
                    (ident.toInt() shr 8) or ((ident and 0b00000000_00000011).toInt() shl 6)
                byteArrayOf(first.toByte() or 0b01000000, second.toByte())
            }
            else -> throw IllegalArgumentException("Reserved for future address format extensions")
        }

        val hash = (PREFIX + addressTypeByteArray + normalizedKey).blake2b512()
        val checksum = hash.copyOfRange(0, PREFIX_SIZE)

        val resultByteArray = addressTypeByteArray + normalizedKey + checksum

        return base58.encode(resultByteArray)
    }

    @Throws(IllegalArgumentException::class)
    fun decode(ss58String: String): ByteArray {
        val decodedByteArray = base58.decode(ss58String)
        if (decodedByteArray.size < 2) throw IllegalArgumentException("Invalid address")
        val (prefixLen, _) = getPrefixLenIdent(decodedByteArray)
        val hash =
            (PREFIX + decodedByteArray.copyBytes(0, PUBLIC_KEY_SIZE + prefixLen)).blake2b512()
        val checkSum = hash.copyBytes(0, PREFIX_SIZE)
        if (!checkSum.contentEquals(
                decodedByteArray.copyBytes(
                    PUBLIC_KEY_SIZE + prefixLen,
                    PREFIX_SIZE
                )
            )
        ) {
            throw IllegalArgumentException("Invalid checksum")
        }
        return decodedByteArray.copyBytes(prefixLen, PUBLIC_KEY_SIZE)
    }

    @Throws(AddressFormatException::class)
    fun extractAddressByte(address: String): Short {
        val decodedByteArray = base58.decode(address)
        if (decodedByteArray.size < 2) throw IllegalArgumentException("Invalid address")
        val (_, ident) = getPrefixLenIdent(decodedByteArray)
        return ident
    }

    private fun extractAddressByteOrNull(address: String): Short? = try {
        extractAddressByte(address)
    } catch (e: Exception) {
        null
    }

    private fun ByteArray.toAddress(addressByte: Short) = encode(this, addressByte)

    fun String.toAccountId(): ByteArray = decode(this)

    fun String.addressByte(): Short = extractAddressByte(this)

    fun String.addressByteOrNull(): Short? = extractAddressByteOrNull(this)

    fun String.accountIdHexToAddress(addressByte: Short) = when (this.startsWith("0x")) {
        true -> this.substring(2)
        false -> this
    }.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
        .toAddress(addressByte)
}