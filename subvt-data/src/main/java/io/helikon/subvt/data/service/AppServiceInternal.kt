package io.helikon.subvt.data.service

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.model.app.CreateDefaultUserNotificationRulesRequest
import io.helikon.subvt.data.model.app.CreateUserNotificationRuleRequest
import io.helikon.subvt.data.model.app.Network
import io.helikon.subvt.data.model.app.NewUserNotificationChannel
import io.helikon.subvt.data.model.app.NewUserValidator
import io.helikon.subvt.data.model.app.NotificationChannel
import io.helikon.subvt.data.model.app.NotificationType
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.model.app.UserNotificationChannel
import io.helikon.subvt.data.model.app.UserNotificationRule
import io.helikon.subvt.data.model.app.UserValidator
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.model.substrate.AccountIdDeserializer
import io.helikon.subvt.data.model.substrate.AccountIdSerializer
import io.helikon.subvt.data.service.auth.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * SubVT application service client for internal use.
 * AppService class is the public interface to be used by the clients.
 */
internal interface AppServiceInternal {
    @GET("network")
    suspend fun getNetworks(): Response<List<Network>>

    @GET("notification/channel")
    suspend fun getNotificationChannels(): Response<List<NotificationChannel>>

    @GET("notification/type")
    suspend fun getNotificationTypes(): Response<List<NotificationType>>

    @POST("secure/user")
    suspend fun createUser(): Response<User>

    @GET("secure/user/notification/channel")
    suspend fun getUserNotificationChannels(): Response<List<UserNotificationChannel>>

    @POST("secure/user/notification/channel")
    suspend fun createUserNotificationChannel(
        @Body channel: NewUserNotificationChannel,
    ): Response<UserNotificationChannel>

    @DELETE("secure/user/notification/channel/{id}")
    suspend fun deleteUserNotificationChannel(
        @Path("id") id: Long,
    ): Response<Void>

    @GET("secure/user/validator")
    suspend fun getUserValidators(): Response<List<UserValidator>>

    @POST("secure/user/validator")
    suspend fun createUserValidator(
        @Body validator: NewUserValidator,
    ): Response<UserValidator>

    @DELETE("secure/user/validator/{id}")
    suspend fun deleteUserValidator(
        @Path("id") id: Long,
    ): Response<Void>

    @GET("secure/user/notification/rule")
    suspend fun getUserNotificationRules(): Response<List<UserNotificationRule>>

    @POST("secure/user/notification/rule")
    suspend fun createUserNotificationRule(
        @Body request: CreateUserNotificationRuleRequest,
    ): Response<UserNotificationRule>

    @DELETE("secure/user/notification/rule/{id}")
    suspend fun deleteUserNotificationRule(
        @Path("id") id: Long,
    ): Response<Void>

    @POST("secure/user/notification/rule/default")
    suspend fun createDefaultUserNotificationRules(
        @Body request: CreateDefaultUserNotificationRulesRequest,
    ): Response<Void>

    companion object {
        private var service: AppServiceInternal? = null

        fun getInstance(
            context: Context,
            baseURL: String,
        ): AppServiceInternal {
            if (service == null) {
                val logInterceptor = HttpLoggingInterceptor()
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClientBuilder = OkHttpClient.Builder()
                httpClientBuilder
                    .addInterceptor(AuthInterceptor(context))
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
                service = retrofit.create(AppServiceInternal::class.java)
            }
            return service!!
        }
    }
}
