package io.helikon.subvt.data

import io.helikon.subvt.data.service.ReportService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class ReportServiceTest {

    @ExperimentalCoroutinesApi
    @Test
    fun testGetMultipleEraReport() = runTest {
        val reportService = ReportService.getInstance("http://78.181.100.160:17900/")
        val response = reportService.getEraReport(3201, 3205)
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.size ?: 0, 5)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetMultipleEraValidatorReport() = runTest {
        val reportService = ReportService.getInstance("http://78.181.100.160:17900/")
        val response = reportService.getEraValidatorReport(
            "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116",
            3201,
            3205
        )
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.size ?: 0, 5)
    }

}