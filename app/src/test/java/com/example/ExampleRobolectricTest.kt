package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.privacy.TrackerBlocker
import com.example.privacy.TrackerCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Browser RN", appName)
    }

    @Test
    fun `tracker blocker identifies doubleclick ads`() {
        val result = TrackerBlocker.checkUrl("https://googleads.g.doubleclick.net/pagead/ads?client=ca-pub")
        assertNotNull(result)
        assertEquals(TrackerCategory.AD_NETWORK, result?.category)
    }

    @Test
    fun `tracker blocker identifies google analytics`() {
        val result = TrackerBlocker.checkUrl("https://www.google-analytics.com/analytics.js")
        assertNotNull(result)
        assertEquals(TrackerCategory.ANALYTICS, result?.category)
    }
}
