package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.model.substrate.EraReport
import io.helikon.subvt.data.model.substrate.EraValidatorReport
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * SubVT report service client for internal use.
 * ReportService class is the public interface to be used by the clients.
 */
internal interface ReportServiceInternal {
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
    ): Response<List<EraValidatorReport>>

    companion object {
        private var service: ReportServiceInternal? = null
        fun getInstance(baseURL: String): ReportServiceInternal {
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
                service = retrofit.create(ReportServiceInternal::class.java)
            }
            return service!!
        }
    }
}