package io.helikon.subvt.data

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.helikon.subvt.data.model.app.NewUserNotificationChannel
import io.helikon.subvt.data.model.app.NotificationChannel
import io.helikon.subvt.data.service.AppService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AppServiceInstrumentedTest {

    companion object {
        val service = AppService.getInstance(
            InstrumentationRegistry.getInstrumentation().targetContext,
            "http://10.0.2.2:7901/"
        )
    }

    @Test
    fun test01GetNetworks() = runTest {
        val response = service.getNetworks()
        assertTrue(response.isSuccessful)
        assertTrue(service.getNetworks().body()?.size ?: 0 > 0)
    }

    @Test
    fun test02CreateUser() = runTest {
        val response = service.createUser()
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.id ?: 0 > 0)
        assertTrue(response.body()?.publicKeyHex?.length ?: 0 > 0)
    }

    @Test
    fun test03GetAndCreateUserNotificationChannels() = runTest {
        // empty channel list
        var listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccessful)
        assertEquals(0, listResponse.body()!!.size)
        // create a channel
        val newChannel = NewUserNotificationChannel(
            NotificationChannel.GSM,
            "+905321234567"
        )
        val createResponse = service.createUserNotificationChannel(newChannel)
        assertTrue(createResponse.isSuccessful)
        assertEquals(createResponse.body()?.target, newChannel.target)
        // channel list with 1 element
        listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccessful)
        assertEquals(1, listResponse.body()!!.size)
    }
}