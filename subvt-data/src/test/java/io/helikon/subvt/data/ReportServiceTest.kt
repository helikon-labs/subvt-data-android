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
        const val validatorAccountId = "0xa00505eb2a4607f27837f57232f0c456602e39540582685b4f58cde293f1a116"

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
            validatorAccountId,
            -5,
            -2
        )
        assertFalse(response.isSuccess)
    }

    @Test
    fun testGetSingleEraValidatorReport() = runTest {
        val response = service.getEraValidatorReport(
            validatorAccountId,
            3391,
            null
        )
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 1)
    }

    @Test
    fun testGetMultipleEraValidatorReport() = runTest {
        val response = service.getEraValidatorReport(
            validatorAccountId,
            3391,
            3395
        )
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 5)
    }

    @Test
    fun testGetValidatorDetails() = runTest {
        val response = service.getValidatorDetails(
            validatorAccountId,
        )
        assertTrue(response.isSuccess)
        assertEquals(
            response.getOrNull()?.validatorDetails?.account?.id?.toString()?.lowercase(),
            validatorAccountId.lowercase()
        )
    }

    @Test
    fun testGetValidatorSummary() = runTest {
        val response = service.getValidatorSummary(
            validatorAccountId,
        )
        assertTrue(response.isSuccess)
        assertEquals(
            response.getOrNull()?.validatorSummary?.accountId?.toString()?.lowercase(),
            validatorAccountId.lowercase()
        )
    }

    @Test
    fun testGetValidatorList() = runTest {
        val response = service.getValidatorList()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.validators?.size ?: 0) > 0)
    }

    @Test
    fun testGetActiveValidatorList() = runTest {
        val response = service.getActiveValidatorList()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.validators?.size ?: 0) > 0)
    }

    @Test
    fun testGetInactiveValidatorList() = runTest {
        val response = service.getInactiveValidatorList()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.validators?.size ?: 0) > 0)
    }

    @Test
    fun testSearchValidators() = runTest {
        val response = service.searchValidators("elik")
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun testGetOneKVNominatorSummaries() = runTest {
        val response = service.getOneKVNominatorSummaries()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun testGetAllEras() = runTest {
        val response = service.getAllEras()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun testGetCurrentEra() = runTest {
        val response = service.getCurrentEra()
        assertTrue(response.isSuccess)
        assertTrue(response.getOrNull() != null)
    }

    @Test
    fun testGetValidatorEraRewardReport() = runTest {
        val response = service.getValidatorEraRewardReport(
            validatorAccountId,
        )
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun testGetValidatorEraPayoutReport() = runTest {
        val response = service.getValidatorEraPayoutReport(
            validatorAccountId,
        )
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun testGetSingleSessionValidatorReport() = runTest {
        val response = service.getSessionValidatorReport(
            validatorAccountId,
            27582,
            null
        )
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 1)
    }

    @Test
    fun testGetMultipleSessionValidatorReport() = runTest {
        val response = service.getSessionValidatorReport(
            validatorAccountId,
            27582,
            27591
        )
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.size ?: 0, 10)
    }

    @Test
    fun testGetCurrentSession() = runTest {
        val response = service.getCurrentSession()
        assertTrue(response.isSuccess)
        assertTrue(response.getOrNull() != null)
    }
}
