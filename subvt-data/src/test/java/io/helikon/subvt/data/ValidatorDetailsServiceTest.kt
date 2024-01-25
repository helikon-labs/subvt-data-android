package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.model.app.ValidatorDetails
import io.helikon.subvt.data.model.app.ValidatorDetailsDiff
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ValidatorDetailsService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalCoroutinesApi
class ValidatorDetailsServiceTest {
    companion object {
        init {
            val formatStrategy =
                PrettyFormatStrategy.newBuilder()
                    .logStrategy { _, _, message -> println(message) }
                    .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        }
    }

    @Test
    fun testValidatorDetails() =
        runTest(timeout = (5 * 60 * 1000).milliseconds) {
            val validatorAccountId =
                "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"
            var updateCount = 0
            val updateCountLimit = 2
            val listener =
                object : RPCSubscriptionListener<ValidatorDetails, ValidatorDetailsDiff> {
                    override suspend fun onSubscribed(
                        service: RPCSubscriptionService<ValidatorDetails, ValidatorDetailsDiff>,
                        subscriptionId: Long,
                        bestBlockNumber: Long?,
                        finalizedBlockNumber: Long?,
                        data: ValidatorDetails,
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
                        update: ValidatorDetailsDiff?,
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
                        subscriptionId: Long,
                    ) {
                        // no-op
                    }
                }
            val service =
                ValidatorDetailsService(listener)
            service.subscribe(
                BuildConfig.RPC_HOST,
                BuildConfig.VALIDATOR_DETAILS_SERVICE_PORT,
                listOf(validatorAccountId),
            )
            assertTrue(updateCount >= updateCountLimit)
        }
}
