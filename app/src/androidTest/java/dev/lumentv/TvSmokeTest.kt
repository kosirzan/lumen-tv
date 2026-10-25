package dev.lumentv

import android.content.Intent
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TvSmokeTest {
    private val context get() = ApplicationProvider.getApplicationContext<android.content.Context>()
    @Test fun localWatchlistAndProgressPersist() {
        val store = Store(context); store.clearHistory()
        val title = Title(999999,"movie","Instrumentation fixture")
        if(store.contains(title)) store.toggle(title)
        assertTrue(store.toggle(title)); assertTrue(Store(context).contains(title))
        assertFalse(store.toggle(title)); assertFalse(store.contains(title))
        store.save(Playback(title,seconds=100.0,duration=1000.0))
        assertEquals(100.0,Store(context).resume(title).seconds,0.1)
        store.save(Playback(title,seconds=960.0,duration=1000.0))
        assertFalse(Store(context).resume(title).resumable)
    }
    @Test fun dpadHomeToSettingsAndBack() {
        ActivityScenario.launch(MainActivity::class.java).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            assertTrue(device.wait(Until.hasObject(By.text("Settings")),15000))
            // Initial focus is Home; every navigation action is a D-pad event.
            repeat(5) { device.pressDPadRight() }; device.pressDPadCenter()
            assertTrue(device.wait(Until.hasObject(By.text("Make yourself at home.")),10000))
            device.pressBack()
            assertTrue(device.wait(Until.hasObject(By.text("L U M E N")),10000))
        }
    }
    @Test fun offlinePlayerAdvancesAndPausesViaRemote() {
        val intent = Intent(context,PlayerActivity::class.java).putExtra("demo",true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ActivityScenario.launch<PlayerActivity>(intent).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            assertTrue(device.wait(Until.hasObject(By.text("Pause")),15000))
            assertTrue(device.wait(Until.hasObject(By.text(java.util.regex.Pattern.compile(".*0:00:0[2-9] / 0:00:20.*"))),30000))
            device.takeScreenshot(java.io.File(context.filesDir,"player.png"))
            device.pressDPadCenter()
            assertTrue("Select should activate focused Pause",device.wait(Until.hasObject(By.text("Play")),10000))
            device.pressDPadRight(); device.pressDPadCenter()
            device.pressDPadLeft(); device.pressDPadCenter()
            assertTrue(device.wait(Until.hasObject(By.text("Pause")),5000))
            device.pressBack()
        }
    }
}
