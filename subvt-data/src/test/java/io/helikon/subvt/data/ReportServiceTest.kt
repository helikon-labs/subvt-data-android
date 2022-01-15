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
        val call = reportService.getEraReport(3101, 3105)
        assertTrue(call.isSuccessful)
        assertEquals(call.body()?.size ?: 0, 5)
    }

}