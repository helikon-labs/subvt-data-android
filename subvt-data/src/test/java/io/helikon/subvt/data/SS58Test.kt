package io.helikon.subvt.data

import io.helikon.subvt.data.ss58.SS58Encoder.addressByte
import io.helikon.subvt.data.ss58.SS58Encoder.addressByteOrNull
import io.helikon.subvt.data.ss58.SS58Encoder.hexToAddress
import io.helikon.subvt.data.ss58.SS58Encoder.hexToBytes
import io.helikon.subvt.data.ss58.SS58Encoder.toAccountId
import org.junit.Assert.*
import org.junit.Test

class SS58Test {

    @Test
    fun testSS58() {
        val validatorAccountId =
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"
        val address = validatorAccountId.hexToAddress(2)
        assertEquals("GC8fuEZG4E5epGf5KGXtcDfvrc6HXE7GJ5YnbiqSpqdQYLg", address)
        assertEquals(address.addressByte().toInt(), 2)
        assertEquals(address.addressByteOrNull()?.toInt(), 2)
        assertTrue(address.toAccountId().contentEquals(validatorAccountId.hexToBytes()))
    }

}