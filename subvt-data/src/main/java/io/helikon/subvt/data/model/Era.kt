package io.helikon.subvt.data.model

import com.google.gson.annotations.SerializedName

data class Era(
    @SerializedName("index")
    val index: Int,
    @SerializedName("start_timestamp")
    val startTimestamp: Long,
    @SerializedName("end_timestamp")
    val endTimestamp: Long,
)