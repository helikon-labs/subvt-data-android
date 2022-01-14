package io.helikon.subvt.data.service

import io.helikon.subvt.data.model.EraReport
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportService {
    @GET("report/era")
    fun getEraReport(
        @Query("start_era_index") startEraIndex: Int,
        @Query("end_era_index") endEraIndex: Int?,
    ): Call<List<EraReport>>
}