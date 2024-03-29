/**
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helikon.subvt.data.encrypt

import io.helikon.subvt.data.exception.AddressFormatException
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

internal class Base58 {
    private val alphabet =
        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val indices = IntArray(128)
    private var digest: MessageDigest? = null

    init {
        Arrays.fill(indices, -1)
        for (i in alphabet.indices) {
            indices[alphabet[i].code] = i
        }

        try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }.also { digest = it }
    }

    fun encode(input: ByteArray): String {
        if (input.isEmpty()) {
            return ""
        }
        val inputCopy = copyOfRange(input, 0, input.size)
        var zeroCount = 0
        while (zeroCount < inputCopy.size && inputCopy[zeroCount].toInt() == 0) {
            ++zeroCount
        }

        val temp = ByteArray(inputCopy.size * 2)
        var j = temp.size
        var startAt = zeroCount
        while (startAt < inputCopy.size) {
            val mod =
                divmod58(inputCopy, startAt)
            if (inputCopy[startAt].toInt() == 0) {
                ++startAt
            }
            temp[--j] =
                alphabet[mod.toInt()].code.toByte()
        }

        while (j < temp.size && temp[j].toInt() == alphabet[0].code) {
            ++j
        }

        while (--zeroCount >= 0) {
            temp[--j] = alphabet[0].code.toByte()
        }
        val output =
            copyOfRange(temp, j, temp.size)
        return try {
            String(output, Charsets.US_ASCII)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }

    @Throws(AddressFormatException::class)
    fun decode(input: String): ByteArray {
        if (input.isEmpty()) {
            return ByteArray(0)
        }
        val input58 = ByteArray(input.length)

        for (i in input.indices) {
            val c = input[i]
            var digit58 = -1
            if (c.code in 0..127) {
                digit58 = indices[c.code]
            }
            if (digit58 < 0) {
                throw AddressFormatException("Illegal character $c at $i")
            }
            input58[i] = digit58.toByte()
        }

        var zeroCount = 0
        while (zeroCount < input58.size && input58[zeroCount].toInt() == 0) {
            ++zeroCount
        }

        val temp = ByteArray(input.length)
        var j = temp.size
        var startAt = zeroCount
        while (startAt < input58.size) {
            val mod = divmod256(input58, startAt)
            if (input58[startAt].toInt() == 0) {
                ++startAt
            }
            temp[--j] = mod
        }

        while (j < temp.size && temp[j].toInt() == 0) {
            ++j
        }
        return copyOfRange(
            temp,
            j - zeroCount,
            temp.size,
        )
    }

    private fun divmod58(
        number: ByteArray,
        startAt: Int,
    ): Byte {
        var remainder = 0
        for (i in startAt until number.size) {
            val digit256 = number[i].toInt() and 0xFF
            val temp = remainder * 256 + digit256
            number[i] = (temp / 58).toByte()
            remainder = temp % 58
        }
        return remainder.toByte()
    }

    private fun divmod256(
        number58: ByteArray,
        startAt: Int,
    ): Byte {
        var remainder = 0
        for (i in startAt until number58.size) {
            val digit58 = number58[i].toInt() and 0xFF
            val temp = remainder * 58 + digit58
            number58[i] = (temp / 256).toByte()
            remainder = temp % 256
        }
        return remainder.toByte()
    }

    private fun copyOfRange(
        source: ByteArray,
        from: Int,
        to: Int,
    ): ByteArray {
        val range = ByteArray(to - from)
        System.arraycopy(source, from, range, 0, range.size)
        return range
    }
}
