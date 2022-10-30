package io.helikon.subvt.data

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.helikon.subvt.data.exception.apiException
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
        private val service = AppService(
            context,
            "https://${BuildConfig.API_HOST}:${BuildConfig.APP_SERVICE_PORT}/"
        )

        init {
            clearKeys(context)
        }
    }

    @Test
    fun test01GetNetworks() = runTest {
        val result = service.getNetworks()
        assertTrue(result.isSuccess)
        assertTrue((result.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun test02GetNotificationChannels() = runTest {
        val response = service.getNotificationChannels()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun test03GetNotificationTypes() = runTest {
        val response = service.getNotificationTypes()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.size ?: 0) > 0)
    }

    @Test
    fun test04CreateUser() = runTest {
        val response = service.createUser()
        assertTrue(response.isSuccess)
        assertTrue((response.getOrNull()?.id ?: 0) > 0)
        assertTrue((response.getOrNull()?.publicKeyHex?.length ?: 0) > 0)
    }

    @Test
    fun test05GetUserNotificationChannels() = runTest {
        val listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccess)
        assertEquals(0, listResponse.getOrNull()!!.size)
    }

    @Test
    fun test06CreateUserNotificationChannel() = runTest {
        val gsmChannel = NewUserNotificationChannel(
            NotificationChannel.GSM,
            "+905321234567",
        )
        val createResponse = service.createUserNotificationChannel(gsmChannel)
        assertTrue(createResponse.isSuccess)
        assertEquals(createResponse.getOrNull()?.target, gsmChannel.target)
        val listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccess)
        assertEquals(1, listResponse.getOrNull()!!.size)
        assertEquals(
            listResponse.getOrNull()!![0].channel,
            NotificationChannel.GSM,
        )
    }

    @Test
    fun test07DeleteUserNotificationChannel() = runTest {
        val id = service.getUserNotificationChannels().getOrNull()!![0].id
        val deleteResponse = service.deleteUserNotificationChannel(id)
        assertTrue(deleteResponse.isSuccess)
        val listResponse = service.getUserNotificationChannels()
        assertTrue(listResponse.isSuccess)
        assertEquals(0, listResponse.getOrNull()!!.size)
    }

    @Test
    fun test08DeleteNonExistingUserNotificationChannel() = runTest {
        val deleteResponse = service.deleteUserNotificationChannel(157)
        assertEquals(404, deleteResponse.apiException()?.code ?: 0)
        assertFalse(deleteResponse.isSuccess)
    }

    @Test
    fun test09GetUserValidators() = runTest {
        val listResponse = service.getUserValidators()
        assertTrue(listResponse.isSuccess)
        assertEquals(0, listResponse.getOrNull()!!.size)
    }

    @Test
    fun test10CreateUserValidator() = runTest {
        val networkId = service.getNetworks().getOrNull()!![0].id
        val userValidator = NewUserValidator(
            networkId,
            AccountId("1ead682c90db49f1145129109b759a3b80fef1aea0914982bd76ecd365bfa629"),
        )
        val createResponse = service.createUserValidator(userValidator)
        assertTrue(createResponse.isSuccess)
        assertEquals(createResponse.getOrNull()?.validatorAccountId, userValidator.validatorAccountId)
        val listResponse = service.getUserValidators()
        assertTrue(listResponse.isSuccess)
        assertEquals(1, listResponse.getOrNull()!!.size)
    }

    @Test
    fun test11DeleteUserValidator() = runTest {
        val id = service.getUserValidators().getOrNull()!![0].id
        val deleteResponse = service.deleteUserValidator(id)
        assertTrue(deleteResponse.isSuccess)
        val listResponse = service.getUserValidators()
        assertTrue(listResponse.isSuccess)
        assertEquals(0, listResponse.getOrNull()!!.size)
    }

    @Test
    fun test12DeleteNonExistingUserValidator() = runTest {
        val deleteResponse = service.deleteUserValidator(157)
        assertEquals(404, deleteResponse.apiException()?.code ?: 0)
        assertFalse(deleteResponse.isSuccess)
    }

    @Test
    fun test13GetUserNotificationRules() = runTest {
        val listResponse = service.getUserNotificationRules()
        assertTrue(listResponse.isSuccess)
        assertEquals(0, listResponse.getOrNull()!!.size)
    }

    @Test
    fun test14CreateAndDeleteUserNotificationRule() = runTest {
        // create channel
        val gsmChannel = NewUserNotificationChannel(
            NotificationChannel.GSM,
            "+905329999999",
        )
        service.createUserNotificationChannel(gsmChannel)
        val notificationType = service.getNotificationTypes().getOrNull()!!
            .find {
                it.code == "chain_validator_new_nomination"
            }!!
        val channelId = service.getUserNotificationChannels().getOrNull()!!.last().id
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
                    "12345",
                )
            ),
            "Notes"
        )
        val response = service.createUserNotificationRule(request)
        assertTrue(response.isSuccess)
        assertEquals(response.getOrNull()?.notificationType?.code ?: "", notificationType.code)
        val ruleId = response.getOrNull()!!.id
        val deleteResponse = service.deleteUserNotificationRule(ruleId)
        assertTrue(deleteResponse.isSuccess)
    }

    /*
    @Test
    fun test15DeleteUserNotificationRule() = runTest {
        val id = service.getUserNotificationRules().getOrNull()!![0].id
        val deleteResponse = service.deleteUserNotificationRule(id)
        assertTrue(deleteResponse.isSuccess)
        val rules = service.getUserNotificationRules()
        assertFalse(rules.getOrNull()?.isEmpty() ?: true)
    }
     */

    @Test
    fun test16DeleteNonExistingUserNotificationRule() = runTest {
        val deleteResponse = service.deleteUserNotificationRule(157)
        assertEquals(404, deleteResponse.apiException()?.code ?: 0)
        assertFalse(deleteResponse.isSuccess)
    }

    @Test
    fun test17CreateDefaultUserNotificationRules() = runTest {
        // create channel
        val apnsChannel = NewUserNotificationChannel(
            NotificationChannel.APNS,
            "ASD-1234-FGD-5453",
        )
        val createChannelResponse = service.createUserNotificationChannel(apnsChannel)
        assertTrue(createChannelResponse.isSuccess)
        val channelId = createChannelResponse.getOrNull()?.id ?: 0
        assertTrue(channelId > 0)
        val request = CreateDefaultUserNotificationRulesRequest(channelId)
        assertTrue(service.createDefaultUserNotificationRules(request).isSuccess)
        val listResponse = service.getUserNotificationRules()
        assertTrue(listResponse.isSuccess)
        assertTrue((listResponse.getOrNull()?.size ?: 0) > 20)
    }
}