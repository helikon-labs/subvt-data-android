package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.model.ValidatorDetails
import io.helikon.subvt.data.model.ValidatorDetailsDiff
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ValidatorDetailsService
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
    fun testValidatorDetails() = runTest(dispatchTimeoutMs = 5 * 60 * 1000) {
        val validatorAccountId =
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"
        var updateCount = 0
        val updateCountLimit = 2
        val listener = object : RPCSubscriptionListener<ValidatorDetails, ValidatorDetailsDiff> {
            override suspend fun onSubscribed(
                service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
                subscriptionId: Long,
                bestBlockNumber: Long?,
                finalizedBlockNumber: Long?,
                data: ValidatorDetails
            ) {
                Logger.d(
                    "Subscribed at finalized block #%d.\nValidator details: %s",
                    finalizedBlockNumber,
                    data,
                )
            }

            override suspend fun onUpdateReceived(
                service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
                subscriptionId: Long,
                bestBlockNumber: Long?,
                finalizedBlockNumber: Long?,
                update: ValidatorDetailsDiff?
            ) {
                Logger.d(
                    "Update at finalized block #%d.\nValidator details diff: %s",
                    finalizedBlockNumber,
                    update,
                )
                updateCount++
                if (updateCount == updateCountLimit) {
                    Logger.d("Unsubscribe from validator list.")
                    service.unsubscribe()
                }
            }

            override suspend fun onUnsubscribed(
                service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
                subscriptionId: Long
            ) {
                // no-op
            }
        }
        val service = ValidatorDetailsService(
            "78.181.100.160",
            17891,
            listener,
        )
        service.subscribe(listOf(validatorAccountId))
        assertEquals(updateCountLimit, updateCount)
    }

}