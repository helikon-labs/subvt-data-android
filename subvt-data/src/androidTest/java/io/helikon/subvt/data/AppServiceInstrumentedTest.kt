package io.helikon.subvt.data

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.helikon.subvt.data.service.AppService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AppServiceInstrumentedTest {

    companion object {
        val service = AppService.getInstance(
            InstrumentationRegistry.getInstrumentation().targetContext,
            "http://192.168.0.101:17901/"
        )
    }

    @Test
    fun testGetNetworks() = runTest {
        val response = service.getNetworks()
        assertTrue(response.isSuccessful)
        assertTrue(service.getNetworks().body()?.size ?: 0 > 0)
    }

    @Test
    fun testCreateUser() = runTest {
        val response = service.createUser()
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.id ?: 0 > 0)
        assertTrue(response.body()?.publicKeyHex?.length ?: 0 > 0)
    }
}