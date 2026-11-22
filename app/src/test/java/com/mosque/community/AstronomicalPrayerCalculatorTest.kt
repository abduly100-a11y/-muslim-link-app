package com.mosque.community

import com.mosque.community.data.models.CalculationMethod
import com.mosque.community.util.prayer.AstronomicalPrayerCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class AstronomicalPrayerCalculatorTest {

    @Test
    fun testQiblaBearingSpringfield() {
        // Springfield, IL coordinates: 39.7817, -89.6501
        val bearing = AstronomicalPrayerCalculator.calculateQiblaBearing(39.7817, -89.6501)
        // Expected Qibla bearing from central Illinois is ~58° ENE
        assertTrue("Bearing should be near 58 degrees", bearing in 56f..60f)
    }

    @Test
    fun testDistanceToKaaba() {
        // Distance from central Illinois to Makkah is ~6,800 - 6,900 miles
        val distance = AstronomicalPrayerCalculator.calculateDistanceToKaabaMiles(39.7817, -89.6501)
        assertTrue("Distance should be roughly 6800-6900 miles", distance in 6700..7000)
    }

    @Test
    fun testDistanceBetweenCoordinates() {
        // Distance between Springfield, IL (39.7817, -89.6501) and Chicago, IL (41.8781, -87.6298) is ~180 miles
        val distance = AstronomicalPrayerCalculator.calculateDistanceBetweenMiles(
            39.7817, -89.6501,
            41.8781, -87.6298
        )
        assertTrue("Distance to Chicago should be ~180 miles", distance in 170.0..195.0)
    }

    @Test
    fun testCalculatePrayerTimesISNA() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")).apply {
            set(2026, Calendar.NOVEMBER, 21, 12, 0, 0)
        }
        val result = AstronomicalPrayerCalculator.calculatePrayerTimes(
            latitude = 39.7817,
            longitude = -89.6501,
            timeZone = TimeZone.getTimeZone("America/Chicago"),
            calendar = cal,
            method = CalculationMethod.ISNA
        )

        assertNotNull(result.prayerTimes.fajrAdhan)
        assertNotNull(result.prayerTimes.dhuhrAdhan)
        assertNotNull(result.prayerTimes.asrAdhan)
        assertNotNull(result.prayerTimes.maghribAdhan)
        assertNotNull(result.prayerTimes.ishaAdhan)

        assertTrue(result.scheduleList.size == 5)
        assertEquals("Fajr", result.scheduleList[0].prayerName)
        assertEquals("Dhuhr", result.scheduleList[1].prayerName)
        assertEquals("Asr", result.scheduleList[2].prayerName)
        assertEquals("Maghrib", result.scheduleList[3].prayerName)
        assertEquals("Isha", result.scheduleList[4].prayerName)

        // At 12:00 PM, next prayer should be Dhuhr or Asr depending on exact solar noon
        assertTrue(result.nextPrayerName == "Dhuhr" || result.nextPrayerName == "Asr")
        assertTrue(result.minutesRemaining > 0)
    }

    @Test
    fun testDifferentCalculationMethodsYieldTimes() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago")).apply {
            set(2026, Calendar.NOVEMBER, 21, 14, 0, 0)
        }
        CalculationMethod.values().forEach { method ->
            val result = AstronomicalPrayerCalculator.calculatePrayerTimes(
                latitude = 39.7817,
                longitude = -89.6501,
                timeZone = TimeZone.getTimeZone("America/Chicago"),
                calendar = cal,
                method = method
            )
            assertNotNull(result.prayerTimes.fajrAdhan)
            assertNotNull(result.nextIqamahTime)
            assertTrue(result.minutesRemaining > 0)
        }
    }
}
