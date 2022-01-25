package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

enum class NotificationParamDataType {
    @SerializedName("string")
    STRING,
    @SerializedName("integer")
    INTEGER,
    @SerializedName("balance")
    BALANCE,
    @SerializedName("float")
    FLOAT,
    @SerializedName("boolean")
    BOOLEAN
}

data class NotificationParamType(
    val id: Long,
    val notificationTypeCode: String,
    val order: Short,
    val code: String,
    val type: NotificationParamDataType,
    val min: String?,
    val max: String?,
    val isOptional: Boolean,
)