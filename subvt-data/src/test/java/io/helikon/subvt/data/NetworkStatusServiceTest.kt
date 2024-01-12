package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.model.app.NetworkStatus
import io.helikon.subvt.data.model.app.NetworkStatusDiff
import io.helikon.subvt.data.service.NetworkStatusService
import io.helikon.subvt.data.service.RPCSubscriptionListener
import io.helikon.subvt.data.service.RPCSubscriptionService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalCoroutinesApi
class NetworkStatusServiceTest {
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
    fun testGetNetworkStatus() =
        runTest(timeout = (5 * 60 * 1000).milliseconds) {
            var updateCount = 0
            var lastBestBlockNumber: Long = 0
            val updateCountLimit = 2
            val listener =
                object : RPCSubscriptionListener<NetworkStatus, NetworkStatusDiff> {
                    override suspend fun onSubscribed(
                        service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
                        subscriptionId: Long,
                        bestBlockNumber: Long?,
                        finalizedBlockNumber: Long?,
                        data: NetworkStatus,
                    ) {
                        Logger.d("Network status received. Best block #${data.bestBlockNumber}.")
                        lastBestBlockNumber = data.bestBlockNumber
                    }

                    override suspend fun onUpdateReceived(
                        service: RPCSubscriptionService<NetworkStatus, NetworkStatusDiff>,
                        subscriptionId: Long,
                        bestBlockNumber: Long?,
                        finalizedBlockNumber: Long?,
                        update: NetworkStatusDiff?,
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
                        subscriptionId: Long,
                    ) {
                        // no-op
                    }
                }
            val service =
                NetworkStatusService(
                    BuildConfig.RPC_HOST,
                    BuildConfig.NETWORK_STATUS_SERVICE_PORT,
                    listener,
                )
            service.subscribe(listOf())
            assertTrue(updateCount >= updateCountLimit)
        }
}
