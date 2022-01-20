package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.service.ReportService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class ReportServiceTest {
    companion object {
        val service = ReportService.getInstance("http://78.181.100.160:17900/")

        init {
            val formatStrategy = PrettyFormatStrategy.newBuilder()
                .logStrategy { _, _, message -> println(message) }
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testEraReportBadParams() = runTest {
        val response = service.getEraReport(3201, -10)
        assertFalse(response.isSuccessful)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetSingleEraReport() = runTest {
        val response = service.getEraReport(3201, null)
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.size ?: 0, 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetMultipleEraReport() = runTest {
        val response = service.getEraReport(3201, 3205)
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.size ?: 0, 5)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testEraValidatorReportBadParams() = runTest {
        val response = service.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            -5,
            -2
        )
        assertFalse(response.isSuccessful)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetSingleEraValidatorReport() = runTest {
        val response = service.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            3201,
            null
        )
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.size ?: 0, 1)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetMultipleEraValidatorReport() = runTest {
        val response = service.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            3201,
            3205
        )
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.size ?: 0, 5)
    }
}