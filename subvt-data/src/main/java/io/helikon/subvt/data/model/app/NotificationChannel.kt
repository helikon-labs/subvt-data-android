package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

enum class NotificationChannelCode {
    @SerializedName("apns")
    APNS,
    @SerializedName("fcm")
    FCM,
    @SerializedName("telegram")
    TELEGRAM,
    @SerializedName("email")
    EMAIL,
    @SerializedName("gsm")
    GSM,
    @SerializedName("sms")
    SMS,
}

/**
 * SubVT user notification channel.
 */
data class NotificationChannel(val code: NotificationChannelCode)