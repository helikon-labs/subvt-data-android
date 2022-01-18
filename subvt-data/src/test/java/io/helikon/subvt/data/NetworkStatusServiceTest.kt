package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.service.RPCService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class NetworkStatusServiceTest {

    companion object {
        private val service = RPCService("78.181.100.160", 17888)

        init {
            val formatStrategy = PrettyFormatStrategy.newBuilder()
                .logStrategy { _, _, message -> println(message) }
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetNetworkStatus() = runTest {
        var updateCount = 0
        var bestBlockNumber: Long = 0
        service.subscribeNetworkStatus { subscriptionId, update ->
            update.status?.let {
                Logger.d("Network status received. Best block #${it.bestBlockNumber}.")
                bestBlockNumber = it.bestBlockNumber
            }
            update.diff?.let {
                Logger.d("Network status update. Best block #${it.bestBlockNumber}.")
                when {
                    it.bestBlockNumber == null -> {
                        service.unsubscribeNetworkStatus(subscriptionId)
                    }
                    it.bestBlockNumber != bestBlockNumber + 1 -> {
                        service.unsubscribeNetworkStatus(subscriptionId)
                    }
                    else -> {
                        bestBlockNumber = it.bestBlockNumber!!
                        updateCount++
                    }
                }
            }
            if (updateCount == 3) {
                Logger.d("Unsubscribe from network status.")
                service.unsubscribeNetworkStatus(subscriptionId)
            }
        }
        assertEquals(3, updateCount)
    }
}