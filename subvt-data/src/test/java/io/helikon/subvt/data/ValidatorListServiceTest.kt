package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.model.ValidatorListUpdate
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import io.helikon.subvt.data.service.ValidatorListService
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

    private suspend fun testValidatorList(
        host: String,
        port: Int,
    ): Boolean {
        var updateCount = 0
        val updateCountLimit = 2
        var firstResponseIsOnlyInsert: Boolean? = null
        val listener = object : RPCSubscriptionListener<ValidatorListUpdate, ValidatorListUpdate> {
            override suspend fun onSubscribed(
                service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
                subscriptionId: Long,
                bestBlockNumber: Long?,
                finalizedBlockNumber: Long?,
                data: ValidatorListUpdate
            ) {
                Logger.d("Subscribed. Insert %d validators.", data.insert.size)
                firstResponseIsOnlyInsert = data.update.isEmpty() && data.removeIds.isEmpty()
            }

            override suspend fun onUpdateReceived(
                service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
                subscriptionId: Long,
                bestBlockNumber: Long?,
                finalizedBlockNumber: Long?,
                update: ValidatorListUpdate?
            ) {
                Logger.d(
                    "Insert %d, update %d, remove %d active validators.",
                    update?.insert?.size,
                    update?.update?.size,
                    update?.removeIds?.size,
                )
                updateCount++
                if (updateCount == updateCountLimit) {
                    Logger.d("Unsubscribe from validator list.")
                    service.unsubscribe()
                }
            }

            override suspend fun onUnsubscribed(
                service: RPCSubscriptionService<ValidatorListUpdate, ValidatorListUpdate>,
                subscriptionId: Long
            ) {
                // no-op
            }
        }
        val service = ValidatorListService(
            host,
            port,
            listener
        )
        service.subscribe(listOf())
        return firstResponseIsOnlyInsert ?: false
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testActiveValidatorList() = runTest(dispatchTimeoutMs = 5 * 60 * 1000) {
        assertTrue(
            testValidatorList(
                "78.181.100.160",
                17889
            )
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testInactiveValidatorList() = runTest(dispatchTimeoutMs = 5 * 60 * 1000) {
        assertTrue(
            testValidatorList(
                "78.181.100.160",
                17890
            )
        )
    }
}