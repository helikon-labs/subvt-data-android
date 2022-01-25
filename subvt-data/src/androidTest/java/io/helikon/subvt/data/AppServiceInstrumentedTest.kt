package io.helikon.subvt.data

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.helikon.subvt.data.model.app.*
import io.helikon.subvt.data.model.substrate.AccountId
import io.helikon.subvt.data.service.AppService
import io.helikon.subvt.data.service.auth.clearKeys
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
        private val context = InstrumentationRegistry.getInstrumentation().targetContext
        val service = AppService.getInstance(
            context,
            "http://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/"
        )

        init {
            clearKeys(context)
        }
    }

    @Test
    fun test01GetNetworks() = runTest {
        val response = service.getNetworks()
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.size ?: 0 > 0)
    }

    @Test
    fun test02GetNotificationChannels() = runTest {
        val response = service.getNotificationChannels()
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.size ?: 0 > 0)
    }

    @Test
    fun test03GetNotificationTypes() = runTest {
        val response = service.getNotificationTypes()
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.size ?: 0 > 0)
    }

    @Test
    fun test04CreateUser() = runTest {
        val response = service.createUser()
        assertTrue(response.isSuccessful)
        assertTrue(response.body()?.id ?: 0 > 0)
        assertTrue(response.body()?.publicKeyHex?.length ?: 0 > 0)
    }

    @Test
    fun test05GetUserNotificationChannels() = runTest {
        val listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccessful)
        assertEquals(0, listResponse.body()!!.size)
    }

    @Test
    fun test06CreateUserNotificationChannel() = runTest {
        val gsmChannel = NewUserNotificationChannel(
            NotificationChannelCode.GSM,
            "+905321234567"
        )
        val createResponse = service.createUserNotificationChannel(gsmChannel)
        assertTrue(createResponse.isSuccessful)
        assertEquals(createResponse.body()?.target, gsmChannel.target)
        val listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccessful)
        assertEquals(1, listResponse.body()!!.size)
    }

    @Test
    fun test07DeleteUserNotificationChannel() = runTest {
        val id = service.getUserNotificationChannels().body()!![0].id
        val deleteResponse = service.deleteUserNotificationChannel(id)
        assertTrue(deleteResponse.isSuccessful)
        val listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccessful)
        assertEquals(0, listResponse.body()!!.size)
    }

    @Test
    fun test08DeleteNonExistingUserNotificationChannel() = runTest {
        val deleteResponse = service.deleteUserNotificationChannel(157)
        assertEquals(404, deleteResponse.code())
        assertFalse(deleteResponse.isSuccessful)
    }

    @Test
    fun test09GetUserValidators() = runTest {
        val listResponse = service.getUserValidators()
        assertTrue(listResponse.isSuccessful)
        assertEquals(0, listResponse.body()!!.size)
    }

    @Test
    fun test10CreateUserValidator() = runTest {
        val networkId = service.getNetworks().body()!![0].id
        val userValidator = NewUserValidator(
            networkId,
            AccountId("1ead682c90db49f1145129109b759a3b80fef1aea0914982bd76ecd365bfa629"),
        )
        val createResponse = service.createUserValidator(userValidator)
        assertTrue(createResponse.isSuccessful)
        assertEquals(createResponse.body()?.validatorAccountId, userValidator.validatorAccountId)
        val listResponse = service.getUserValidators()
        assertTrue(listResponse.isSuccessful)
        assertEquals(1, listResponse.body()!!.size)
    }

    @Test
    fun test11DeleteUserValidator() = runTest {
        val id = service.getUserValidators().body()!![0].id
        val deleteResponse = service.deleteUserValidator(id)
        assertTrue(deleteResponse.isSuccessful)
        val listResponse = service.getUserValidators()
        assertTrue(listResponse.isSuccessful)
        assertEquals(0, listResponse.body()!!.size)
    }

    @Test
    fun test12DeleteNonExistingUserValidator() = runTest {
        val deleteResponse = service.deleteUserValidator(157)
        assertEquals(404, deleteResponse.code())
        assertFalse(deleteResponse.isSuccessful)
    }

    @Test
    fun test13GetUserNotificationRules() = runTest {
        val listResponse = service.getUserNotificationRules()
        assertTrue(listResponse.isSuccessful)
        assertEquals(0, listResponse.body()!!.size)
    }

    @Test
    fun test14CreateUserNotificationRule() = runTest {
        // create channel
        val gsmChannel = NewUserNotificationChannel(
            NotificationChannelCode.GSM,
            "+905329999999"
        )
        service.createUserNotificationChannel(gsmChannel)
        val notificationType = service.getNotificationTypes().body()!!
            .find {
                it.code == "chain_validator_new_nomination"
            }!!
        val channelId = service.getUserNotificationChannels().body()!!.last().id
        // create rule
        val request = CreateUserNotificationRuleRequest(
            notificationType.code,
            "Some notification name",
            null,
            true,
            setOf(),
            NotificationPeriodType.IMMEDIATE,
            0,
            setOf(channelId),
            listOf(
                NewUserNotificationRuleParameter(
                    notificationType.paramTypes[0].id,
                    "12345"
                )
            ),
            "Notes"
        )
        val response = service.createUserNotificationRule(request)
        assertTrue(response.isSuccessful)
        assertEquals(response.body()?.notificationType?.code ?: "", notificationType.code)
    }

    @Test
    fun test15DeleteUserNotificationRule() = runTest {
        val id = service.getUserNotificationChannels().body()!![0].id
        val deleteResponse = service.deleteUserNotificationChannel(id)
        assertTrue(deleteResponse.isSuccessful)
    }

    @Test
    fun test16DeleteNonExistingUserNotificationRule() = runTest {
        val deleteResponse = service.deleteUserNotificationRule(157)
        assertEquals(404, deleteResponse.code())
        assertFalse(deleteResponse.isSuccessful)
    }
}