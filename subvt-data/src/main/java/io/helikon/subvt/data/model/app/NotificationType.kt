package io.helikon.subvt.data.model.app

/**
 * Notification type and its parameters types.
 */
data class NotificationType(
    val code: String,
    val paramTypes: List<NotificationParamType>,
)