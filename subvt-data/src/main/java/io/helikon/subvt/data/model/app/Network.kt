package io.helikon.subvt.data.model.app

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * SubVT network model.
 */
@Parcelize
@Entity(tableName = "network")
data class Network(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "hash")
    val hash: String,
    @ColumnInfo(name = "display")
    val display: String,
    @SerializedName("ss58_prefix")
    @ColumnInfo(name = "ss58_prefix")
    val ss58Prefix: Int,
    @ColumnInfo(name = "token_ticker")
    val tokenTicker: String,
    @ColumnInfo(name = "token_decimal_count")
    val tokenDecimalCount: Int,
    @ColumnInfo(name = "network_status_service_host")
    val networkStatusServiceHost: String? = null,
    @ColumnInfo(name = "network_status_service_port")
    val networkStatusServicePort: Int? = null,
    @ColumnInfo(name = "report_service_host")
    val reportServiceHost: String? = null,
    @ColumnInfo(name = "report_service_port")
    val reportServicePort: Int? = null,
    @ColumnInfo(name = "validator_details_service_host")
    val validatorDetailsServiceHost: String? = null,
    @ColumnInfo(name = "validator_details_service_port")
    val validatorDetailsServicePort: Int? = null,
    @ColumnInfo(name = "active_validator_list_service_host")
    val activeValidatorListServiceHost: String? = null,
    @ColumnInfo(name = "active_validator_list_service_port")
    val activeValidatorListServicePort: Int? = null,
    @ColumnInfo(name = "inactive_validator_list_service_host")
    val inactiveValidatorListServiceHost: String? = null,
    @ColumnInfo(name = "inactive_validator_list_service_port")
    val inactiveValidatorListServicePort: Int? = null,
) : Parcelable
