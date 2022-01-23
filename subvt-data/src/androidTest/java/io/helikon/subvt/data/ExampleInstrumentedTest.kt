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
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() = runTest {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        //assertEquals("io.helikon.subvt.android.data", appContext.packageName)
        val service = AppService.getInstance(appContext, "http://78.181.100.161:17900/")
        service.getNetworks()
    }
}