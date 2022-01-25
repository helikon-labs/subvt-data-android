package io.helikon.subvt.data.model.app

data class NotificationType(
    val code: String,
    val param_types: List<NotificationParamType>,
)