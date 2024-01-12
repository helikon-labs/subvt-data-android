package io.helikon.subvt.data.model.app

/**
 * One of user's notification channels.
 */
data class UserNotificationChannel(
    val id: Long,
    val userId: Long,
    val channel: NotificationChannel,
    val target: String,
)

/**
 * Used when creating a new notification channel for the user.
 */
data class NewUserNotificationChannel(
    val channel: NotificationChannel,
    val target: String,
)
