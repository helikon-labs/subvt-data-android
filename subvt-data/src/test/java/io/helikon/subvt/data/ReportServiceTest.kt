package io.helikon.subvt.data

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.helikon.subvt.data.service.ReportService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ReportServiceTest {
    companion object {

        val service = ReportService(
            "https://${BuildConfig.API_HOST}:${BuildConfig.REPORT_SERVICE_PORT}/"
        )

        init {
            val formatStrategy = PrettyFormatStrategy.newBuilder()
                .logStrategy { _, _, message -> println(message) }
                .build()
            Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        }
    }

    @Test
    fun testEraReportBadParams() = runTest {
        val response = service.getEraReport(3391, -10)
        assertFalse(response.isSuccess)
    }

    @Test
    fun testGetSingleEraReport() = runTest {
        val response = service.getEraReport(3391, null)
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 1)
        val report = response.getOrNull()!![0]
        assertTrue(report.activeValidatorCount > 0)
        assertTrue(report.inactiveValidatorCount > 0)
    }

    @Test
    fun testGetMultipleEraReport() = runTest {
        val response = service.getEraReport(3391, 3395)
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 5)
    }

    @Test
    fun testEraValidatorReportBadParams() = runTest {
        val response = service.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            -5,
            -2
        )
        assertFalse(response.isSuccess)
    }

    @Test
    fun testGetSingleEraValidatorReport() = runTest {
        val response = service.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            3391,
            null
        )
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 1)
    }

    @Test
    fun testGetMultipleEraValidatorReport() = runTest {
        val response = service.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            3391,
            3395
        )
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 5)
    }
}