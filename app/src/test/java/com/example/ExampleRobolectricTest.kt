package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.RiskCategory
import com.example.data.repository.PropertyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LandCheck AI", appName)
    }

    @Test
    fun `property repository returns benchmark properties`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repository = PropertyRepository(db.landCheckDao())

        val properties = repository.getAllProperties()
        assertTrue("Should have benchmark properties", properties.isNotEmpty())

        val salemProp = repository.getPropertyBySurvey("142", "3B", "Salem")
        assertNotNull("Salem property 142/3B should be present", salemProp)
        assertEquals(RiskCategory.HIGH, salemProp?.riskAssessment?.category)

        val maduraiProp = repository.getPropertyBySurvey("215", "1A", "Madurai")
        assertNotNull("Madurai property 215/1A should be present", maduraiProp)
        assertEquals(RiskCategory.LOW, maduraiProp?.riskAssessment?.category)
    }

    @Test
    fun `transparent 5 pillar weights sum to 100`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val repository = PropertyRepository(db.landCheckDao())

        val properties = repository.getAllProperties()
        for (prop in properties) {
            val totalWeight = prop.riskAssessment.pillars.sumOf { it.weightPercent }
            assertEquals("5 Pillars must sum to 100%", 100, totalWeight)
        }
    }

    @Test
    fun `database version 3 initializes cleanly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        assertNotNull(db.landCheckDao())
    }
}
