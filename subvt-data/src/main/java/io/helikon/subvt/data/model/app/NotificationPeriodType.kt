package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

enum class NotificationPeriodType {
    @SerializedName("immediate")
    IMMEDIATE,

    @SerializedName("hour")
    HOUR,

    @SerializedName("day")
    DAY,

    @SerializedName("epoch")
    EPOCH,

    @SerializedName("era")
    ERA,

    @SerializedName("off")
    OFF,
}
