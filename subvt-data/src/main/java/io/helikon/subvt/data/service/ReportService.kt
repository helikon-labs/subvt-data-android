package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.model.EraReport
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportService {
    @GET("report/era")
    suspend fun getEraReport(
        @Query("start_era_index") startEraIndex: Int,
        @Query("end_era_index") endEraIndex: Int?,
    ): Response<List<EraReport>>

    @GET("report/validator/{validator_account_id_hex}")
    suspend fun getEraValidatorReport(
        @Path("validator_account_id_hex") validatorAccountIdHex: String,
        @Query("start_era_index") startEraIndex: Int,
        @Query("end_era_index") endEraIndex: Int?,
    ): Response<List<EraReport>>

    companion object {
        var service: ReportService? = null
        fun getInstance(baseURL: String): ReportService {
            if (service == null) {
                val logInterceptor = HttpLoggingInterceptor()
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClientBuilder = OkHttpClient.Builder()
                httpClientBuilder
                    .networkInterceptors()
                    .add(logInterceptor)
                val gson: Gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(httpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                service = retrofit.create(ReportService::class.java)
            }
            return service!!
        }
    }
}