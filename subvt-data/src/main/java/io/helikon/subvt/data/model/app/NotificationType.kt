package io.helikon.subvt.data.model.app

import kotlinx.collections.immutable.ImmutableList

/**
 * Notification type and its parameters types.
 */
data class NotificationType(
    val code: String,
    val isEnabled: Boolean,
    val paramTypes: ImmutableList<NotificationParamType>,
)
