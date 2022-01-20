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
    fun testKusamaSS58() {
        val validatorAccountId =
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"
        val address = validatorAccountId.hexToAddress(2)
        assertEquals("GC8fuEZG4E5epGf5KGXtcDfvrc6HXE7GJ5YnbiqSpqdQYLg", address)
        assertEquals(address.addressByte().toInt(), 2)
        assertEquals(address.addressByteOrNull()?.toInt(), 2)
        assertTrue(address.toAccountId().contentEquals(validatorAccountId.hexToBytes()))
    }

    @Test
    fun testPolkadotSS58() {
        val validatorAccountId =
            "0x88c0e442d5642eec6a4eb3967161d2af06a2bcf693357acb312c6df2c776283b"
        val address = validatorAccountId.hexToAddress(0)
        assertEquals("146JpAHyC8xvgEpPWny5MGn7Ha2b6g31YiT1WKGTA7j7fAZT", address)
        assertEquals(address.addressByte().toInt(), 0)
        assertEquals(address.addressByteOrNull()?.toInt(), 0)
        assertTrue(address.toAccountId().contentEquals(validatorAccountId.hexToBytes()))
    }
}