package io.helikon.subvt.data.model.app

data class UserNotificationChannel(
    val id: Long,
    val userId: Long,
    val channelCode: NotificationChannelCode,
    val target: String,
)

data class NewUserNotificationChannel(
    val channelCode: NotificationChannelCode,
    val target: String,
)