package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

data class UserNotificationChannel(
    val id: Long,
    val userId: Long,
    @SerializedName("channel_code")
    val channel: NotificationChannel,
    val target: String,
)

data class NewUserNotificationChannel(
    @SerializedName("channel_code")
    val channel: NotificationChannel,
    val target: String,
)