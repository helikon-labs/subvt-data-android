package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

/**
 * SubVT network model.
 */
data class Network(
    val id: Long,
    val hash: String,
    val name: String,
    @SerializedName("ss58_prefix")
    val ss58Prefix: Int,
    val networkStatusServiceHost: String?,
    val networkStatusServicePort: Int?,
    val reportServiceHost: String?,
    val reportServicePort: Int?,
    val validatorDetailsServiceHost: String?,
    val validatorDetailsServicePort: Int?,
    val activeValidatorListServiceHost: String?,
    val activeValidatorListServicePort: Int?,
    val inactiveValidatorListServiceHost: String?,
    val inactiveValidatorListServicePort: Int?,
)
