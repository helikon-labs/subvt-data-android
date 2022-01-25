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
    @SerializedName("network_status_service_url")
    val networkStatusServiceURL: String?,
    @SerializedName("report_service_url")
    val reportServiceURL: String?,
    @SerializedName("validator_details_service_url")
    val validatorDetailsServiceURL: String?,
    @SerializedName("active_validator_list_service_url")
    val activeValidatorListServiceURL: String?,
    @SerializedName("inactive_validator_list_service_url")
    val inactiveValidatorListServiceURL: String?,
)