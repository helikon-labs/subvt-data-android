package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.service.RPCService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ValidatorDetailsServiceTest {

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
    fun testValidatorDetails() = runTest {
        val validatorAccountId = "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"
        val service = RPCService("78.181.100.160", 17891)
        var updateCount = 0
        val updateCountLimit = 3
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