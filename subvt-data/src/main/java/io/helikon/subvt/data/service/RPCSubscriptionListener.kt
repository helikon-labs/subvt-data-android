package io.helikon.subvt.data.service

interface RPCSubscriptionListener<K, T> {
    suspend fun onSubscribed(
        service: RPCSubscriptionService<K, T>,
        subscriptionId: Long,
        bestBlockNumber: Long?,
        finalizedBlockNumber: Long?,
        data: K,
    )

    suspend fun onUpdateReceived(
        service: RPCSubscriptionService<K, T>,
        subscriptionId: Long,
        bestBlockNumber: Long?,
        finalizedBlockNumber: Long?,
        update: T?,
    )

    suspend fun onUnsubscribed(
        service: RPCSubscriptionService<K, T>,
        subscriptionId: Long,
    )
}
