package io.helikon.subvt.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.helikon.subvt.data.model.EraReport
import io.helikon.subvt.data.service.ReportService
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.CountDownLatch

class ReportServiceTest {

    @Test
    fun testGetMultipleEraReport() {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder
            .networkInterceptors()
            .add(logInterceptor)
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://78.181.100.160:17900/")
            .client(httpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val reportService = retrofit.create(ReportService::class.java)
        val call = reportService.getEraReport(3101, 3105)
        val latch = CountDownLatch(1)
        val eraReports = mutableListOf<EraReport>()
        call.enqueue(object : Callback<List<EraReport>> {
            override fun onResponse(
                call: Call<List<EraReport>>,
                response: Response<List<EraReport>>
            ) {
                if (response.code() == 200) {
                    response.body()?.let { eraReports.addAll(it) }
                    latch.countDown()
                }
            }
            override fun onFailure(
                call: Call<List<EraReport>>,
                t: Throwable
            ) {
                latch.countDown()
            }
        })
        latch.await()
        assertEquals(eraReports.size, 5)
    }

}