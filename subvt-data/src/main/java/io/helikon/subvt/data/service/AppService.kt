package io.helikon.subvt.data.service

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.model.app.Network
import io.helikon.subvt.data.model.app.User
import io.helikon.subvt.data.service.auth.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

interface AppService {
    @GET("network")
    suspend fun getNetworks(): Response<List<Network>>

    @POST("secure/user")
    suspend fun createUser(): Response<User>

    companion object {
        private var service: AppService? = null
        fun getInstance(context: Context, baseURL: String): AppService {
            if (service == null) {
                val logInterceptor = HttpLoggingInterceptor()
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                val httpClientBuilder = OkHttpClient.Builder()
                httpClientBuilder
                    .addInterceptor(AuthInterceptor(context))
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
                service = retrofit.create(AppService::class.java)
            }
            return service!!
        }

    }
}