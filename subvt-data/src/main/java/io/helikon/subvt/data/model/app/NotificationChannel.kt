package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

enum class NotificationChannel(val code: String) {
    @SerializedName("apns")
    APNS("apns"),

    @SerializedName("fcm")
    FCM("fcm"),

    @SerializedName("telegram")
    TELEGRAM("telegram"),

    @SerializedName("email")
    EMAIL("email"),

    @SerializedName("gsm")
    GSM("gsm"),

    @SerializedName("sms")
    SMS("sms"),
}
