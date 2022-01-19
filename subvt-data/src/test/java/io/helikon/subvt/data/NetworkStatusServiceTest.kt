package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.model.NetworkStatus
import io.helikon.subvt.data.model.NetworkStatusDiff
import io.helikon.subvt.data.service.NetworkStatusService
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class NetworkStatusServiceTest {

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
    fun testGetNetworkStatus() = runTest(dispatchTimeoutMs = 5 * 60 * 1000) {
        var updateCount = 0
        var lastBestBlockNumber: Long = 0
        val updateCountLimit = 2
        val listener = object : RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff> {
            override suspend fun onSubscribed(
                service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
                subscriptionId: Long,
                bestBlockNumber: Long?,
                finalizedBlockNumber: Long?,
                data: NetworkStatus
            ) {
                Logger.d("Network status received. Best block #${data.bestBlockNumber}.")
                lastBestBlockNumber = data.bestBlockNumber
            }

            override suspend fun onUpdateReceived(
                service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
                subscriptionId: Long,
                bestBlockNumber: Long?,
                finalizedBlockNumber: Long?,
                update: NetworkStatusDiff?
            ) {
                Logger.d("Network status update. Best block #${update?.bestBlockNumber}.")
                when {
                    update?.bestBlockNumber == null -> {
                        service.unsubscribe()
                    }
                    update.bestBlockNumber != lastBestBlockNumber + 1 -> {
                        service.unsubscribe()
                    }
                    else -> {
                        lastBestBlockNumber = update.bestBlockNumber!!
                        updateCount++
                    }
                }
                if (updateCount == updateCountLimit) {
                    Logger.d("Unsubscribe from network status.")
                    service.unsubscribe()
                }
            }

            override suspend fun onUnsubscribed(
                service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
                subscriptionId: Long
            ) {
                // no-op
            }

        }
        val service = NetworkStatusService(
            "78.181.100.160",
            17888,
            listener
        )
        service.subscribe(listOf())
        assertEquals(updateCountLimit, updateCount)
    }

}