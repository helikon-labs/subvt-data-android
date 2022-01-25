package io.helikon.subvt.data.model.app

data class UserNotificationChannel(
    val id: Long,
    val userId: Long,
    val channel_code: NotificationChannelCode,
    val target: String,
)

data class NewUserNotificationChannel(
    val channel_code: NotificationChannelCode,
    val target: String,
)