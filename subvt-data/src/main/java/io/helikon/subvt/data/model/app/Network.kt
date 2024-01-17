package io.helikon.subvt.data.model.app

import com.google.gson.annotations.SerializedName

/**
 * SubVT network model.
 */
data class Network(
    val id: Long,
    val hash: String,
    val display: String,
    @SerializedName("ss58_prefix")
    val ss58Prefix: Int,
    val tokenTicker: String,
    val tokenDecimalCount: Int,
    val networkStatusServiceHost: String? = null,
    val networkStatusServicePort: Int? = null,
    val reportServiceHost: String? = null,
    val reportServicePort: Int? = null,
    val validatorDetailsServiceHost: String? = null,
    val validatorDetailsServicePort: Int? = null,
    val activeValidatorListServiceHost: String? = null,
    val activeValidatorListServicePort: Int? = null,
    val inactiveValidatorListServiceHost: String? = null,
    val inactiveValidatorListServicePort: Int? = null,
)
