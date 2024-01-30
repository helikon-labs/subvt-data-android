package io.helikon.subvt.data.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.model.app.ValidatorSearchSummary
import io.helikon.subvt.data.model.onekv.OneKVNominatorSummary
import io.helikon.subvt.data.model.report.EraReport
import io.helikon.subvt.data.model.report.EraValidatorReport
import io.helikon.subvt.data.model.report.SessionValidatorReport
import io.helikon.subvt.data.model.report.ValidatorDetailsReport
import io.helikon.subvt.data.model.report.ValidatorEraPayoutReport
import io.helikon.subvt.data.model.report.ValidatorEraRewardReport
import io.helikon.subvt.data.model.report.ValidatorListReport
import io.helikon.subvt.data.model.report.ValidatorSummaryReport
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.AccountIdDeserializer
import io.helikon.subvt.data.model.substrate.AccountIdSerializer
import io.helikon.subvt.data.model.substrate.Epoch
import io.helikon.subvt.data.model.substrate.Era
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

    @GET("validator/{validator_account_id_hex}/details")
    suspend fun getValidatorDetails(
        @Path("validator_account_id_hex") validatorAccountIdHex: String,
    ): Response<ValidatorDetailsReport>

    @GET("validator/{validator_account_id_hex}/summary")
    suspend fun getValidatorSummary(
        @Path("validator_account_id_hex") validatorAccountIdHex: String,
    ): Response<ValidatorSummaryReport>

    @GET("validator/list")
    suspend fun getValidatorList(): Response<ValidatorListReport>

    @GET("validator/list/active")
    suspend fun getActiveValidatorList(): Response<ValidatorListReport>

    @GET("validator/list/inactive")
    suspend fun getInactiveValidatorList(): Response<ValidatorListReport>

    @GET("validator/search")
    suspend fun searchValidators(
        @Query("query") query: String,
    ): Response<List<ValidatorSearchSummary>>

    @GET("onekv/nominator")
    suspend fun getOneKVNominatorSummaries(): Response<List<OneKVNominatorSummary>>

    @GET("era")
    suspend fun getAllEras(): Response<List<Era>>

    @GET("era/current")
    suspend fun getCurrentEra(): Response<Era>

    @GET("validator/{validator_account_id_hex}/era/reward")
    suspend fun getValidatorEraRewardReport(
        @Path("validator_account_id_hex") validatorAccountIdHex: String,
    ): Response<List<ValidatorEraRewardReport>>

    @GET("validator/{validator_account_id_hex}/era/payout")
    suspend fun getValidatorEraPayoutReport(
        @Path("validator_account_id_hex") validatorAccountIdHex: String,
    ): Response<List<ValidatorEraPayoutReport>>

    @GET("report/session/validator/{validator_account_id_hex}")
    suspend fun getSessionValidatorReport(
        @Path("validator_account_id_hex") validatorAccountIdHex: String,
        @Query("start_session_index") startSessionIndex: Int,
        @Query("end_session_index") endSessionIndex: Int?,
    ): Response<List<SessionValidatorReport>>

    @GET("session/current")
    suspend fun getCurrentSession(): Response<Epoch>

    companion object {
        fun getInstance(baseURL: String): ReportServiceInternal {
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val httpClientBuilder = OkHttpClient.Builder()
            httpClientBuilder
                .networkInterceptors()
                .add(logInterceptor)
            val gson: Gson =
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .registerTypeAdapter(AccountId::class.java, AccountIdDeserializer())
                    .registerTypeAdapter(AccountId::class.java, AccountIdSerializer())
                    .create()
            val retrofit =
                Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(httpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(ReportServiceInternal::class.java)
        }
    }
}
