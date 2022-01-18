package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.service.RPCService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class ValidatorListServiceTest {

    companion object {
        init {
            val formatStrategy = PrettyFormatStrategy.newBuilder()
                .logStrategy { _, _, message -> println(message) }
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testActiveValidatorList() = runTest {
        Logger.d("Test active validator list subscription.")
        val service = RPCService("78.181.100.160", 17889)
        var updateCount = 0
        val updateCountLimit = 5
        var firstResponseIsOnlyInsert: Boolean? = null
        service.subscribeValidatorList { subscriptionId, update ->
            if (firstResponseIsOnlyInsert == null) {
                firstResponseIsOnlyInsert = update.update.isEmpty() && update.removeIds.isEmpty()
            }
            Logger.d(
                "Insert %d, update %d, remove %d active validators.",
                update.insert.size,
                update.update.size,
                update.removeIds.size,
            )
            updateCount++
            if (updateCount == updateCountLimit) {
                Logger.d("Unsubscribe from validator list.")
                service.unsubscribeValidatorList(subscriptionId)
            }
        }
        assertEquals(updateCountLimit, updateCount)
        assertTrue(firstResponseIsOnlyInsert!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testInactiveValidatorList() = runTest {
        Logger.d("Test inactive validator list subscription.")
        val service = RPCService("78.181.100.160", 17890)
        var updateCount = 0
        val updateCountLimit = 5
        var firstResponseIsOnlyInsert: Boolean? = null
        service.subscribeValidatorList { subscriptionId, update ->
            if (firstResponseIsOnlyInsert == null) {
                firstResponseIsOnlyInsert = update.update.isEmpty() && update.removeIds.isEmpty()
            }
            Logger.d(
                "Insert %d, update %d, remove %d inactive validators.",
                update.insert.size,
                update.update.size,
                update.removeIds.size,
            )
            updateCount++
            if (updateCount == updateCountLimit) {
                Logger.d("Unsubscribe from validator list.")
                service.unsubscribeValidatorList(subscriptionId)
            }
        }
        assertEquals(updateCountLimit, updateCount)
        assertTrue(firstResponseIsOnlyInsert!!)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test03ValidatorDetails() = runTest {
        Logger.d("Test inactive validator details subscription.")
        val service = RPCService("78.181.100.160", 17891)
        var updateCount = 0
        val updateCountLimit = 5
        val validatorAccountId = "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"
        service.subscribeValidatorDetails(validatorAccountId) { subscriptionId, finalized_block_number, details, diff ->
            Logger.d(
                "Update received for finalized block #%d.\nValidator details: %s\nDiff: %s",
                finalized_block_number,
                details ?: "",
                diff ?: "",
            )
            updateCount++
            if (updateCount == updateCountLimit) {
                Logger.d("Unsubscribe from validator list.")
                service.unsubscribeValidatorDetails(subscriptionId)
            }
        }
        assertEquals(updateCountLimit, updateCount)
    }

}