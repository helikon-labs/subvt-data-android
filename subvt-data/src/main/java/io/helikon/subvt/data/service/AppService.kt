package io.helikon.subvt.data.service

import android.content.Context
import io.helikon.subvt.data.model.app.*

/**
 * Public interface for the application service.
 * See AppServiceInternal Retrofit interface for the actual interface.
 */
class AppService(context: Context, baseURL: String) {
    private val service = AppServiceInternal.getInstance(context, baseURL)

    suspend fun getNetworks() =
        extractResponse(
            service.getNetworks()
        )

    suspend fun getNotificationChannels() =
        extractResponse(
            service.getNotificationChannels()
        )

    suspend fun getNotificationTypes() =
        extractResponse(
            service.getNotificationTypes()
        )

    suspend fun createUser() =
        extractResponse(
            service.createUser()
        )

    suspend fun getUserNotificationChannels() =
        extractResponse(
            service.getUserNotificationChannels()
        )

    suspend fun createUserNotificationChannel(channel: NewUserNotificationChannel) =
        extractResponse(
            service.createUserNotificationChannel(channel)
        )

    suspend fun deleteUserNotificationChannel(id: Long) =
        extractResponse(
            service.deleteUserNotificationChannel(id)
        )

    suspend fun getUserValidators() =
        extractResponse(
            service.getUserValidators()
        )

    suspend fun createUserValidator(validator: NewUserValidator) =
        extractResponse(
            service.createUserValidator(validator)
        )

    suspend fun deleteUserValidator(id: Long) =
        extractResponse(
            service.deleteUserValidator(id)
        )

    suspend fun getUserNotificationRules() =
        extractResponse(
            service.getUserNotificationRules()
        )

    suspend fun createUserNotificationRule(request: CreateUserNotificationRuleRequest) =
        extractResponse(
            service.createUserNotificationRule(request)
        )

    suspend fun deleteUserNotificationRule(id: Long) =
        extractResponse(
            service.deleteUserNotificationRule(id)
        )
}