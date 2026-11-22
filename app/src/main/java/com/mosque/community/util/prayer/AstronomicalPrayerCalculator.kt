package com.mosque.community.util.prayer

import com.mosque.community.data.models.CalculationMethod
import com.mosque.community.data.models.PrayerSchedule
import com.mosque.community.data.models.PrayerTimes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object AstronomicalPrayerCalculator {

    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_MILES = 3958.8

    data class PrayerCalculationResult(
        val prayerTimes: PrayerTimes,
        val nextPrayerName: String,
        val nextIqamahTime: String,
        val minutesRemaining: Int,
        val scheduleList: List<PrayerSchedule>
    )

    /**
     * Calculates the Great Circle Qibla bearing in degrees (0-360) from given coordinates to the Kaaba.
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Float {
        val latRad = Math.toRadians(latitude)
        val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
        val diffLongRad = Math.toRadians(KAABA_LONGITUDE - longitude)

        val y = sin(diffLongRad)
        val x = cos(latRad) * tan(kaabaLatRad) - sin(latRad) * cos(diffLongRad)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360.0) % 360.0
        return bearing.toFloat()
    }

    /**
     * Calculates the great-circle distance in miles from given coordinates to the Kaaba.
     */
    fun calculateDistanceToKaabaMiles(latitude: Double, longitude: Double): Int {
        val dLat = Math.toRadians(KAABA_LATITUDE - latitude)
        val dLon = Math.toRadians(KAABA_LONGITUDE - longitude)
        val lat1 = Math.toRadians(latitude)
        val lat2 = Math.toRadians(KAABA_LATITUDE)

        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (EARTH_RADIUS_MILES * c).roundToInt()
    }

    /**
     * Calculates Great Circle distance between any two coordinates in miles.
     */
    fun calculateDistanceBetweenMiles(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(rLat1) * cos(rLat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val dist = EARTH_RADIUS_MILES * c
        return (dist * 10.0).roundToInt() / 10.0
    }

    /**
     * Generates daily prayer times and calculates the dynamic countdown to the next prayer.
     */
    fun calculatePrayerTimes(
        latitude: Double = 39.7817, // Default Springfield, IL
        longitude: Double = -89.6501,
        timeZone: TimeZone = TimeZone.getDefault(),
        calendar: Calendar = Calendar.getInstance(timeZone),
        method: CalculationMethod = CalculationMethod.ISNA
    ): PrayerCalculationResult {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        // Approximate Julian Date
        val julianDate = 367.0 * year - floor(7.0 * (year + floor((calendar.get(Calendar.MONTH) + 1 + 9.0) / 12.0)) / 4.0) +
                calendar.get(Calendar.DAY_OF_MONTH) + 1721013.5

        val d = julianDate - 2451545.0
        val g = (357.529 + 0.98560028 * d) % 360.0
        val q = (280.459 + 0.98564736 * d) % 360.0
        val l = (q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g))) % 360.0

        val e = 23.439 - 0.00000036 * d
        val ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0

        // Equation of time (hours)
        val equationOfTime = (q / 15.0) - ra
        // Solar declination
        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))

        // Solar Noon (hours)
        val timeZoneOffsetHours = timeZone.getOffset(calendar.timeInMillis) / 3600000.0
        val noon = 12.0 + timeZoneOffsetHours - (longitude / 15.0) - equationOfTime

        // Method angle parameters
        val fajrAngle = when (method) {
            CalculationMethod.ISNA -> 15.0
            CalculationMethod.UMM_AL_QURA -> 18.5
            CalculationMethod.MWL -> 18.0
            CalculationMethod.KARACHI -> 18.0
        }
        val ishaAngle = when (method) {
            CalculationMethod.ISNA -> 15.0
            CalculationMethod.UMM_AL_QURA -> 18.0 // Will be overridden to 90 min after Maghrib
            CalculationMethod.MWL -> 17.0
            CalculationMethod.KARACHI -> 18.0
        }

        // Hour angle helper
        fun hourAngle(zenith: Double): Double {
            val num = -sin(Math.toRadians(zenith)) - sin(Math.toRadians(latitude)) * sin(Math.toRadians(declination))
            val den = cos(Math.toRadians(latitude)) * cos(Math.toRadians(declination))
            val cosH = (num / den).coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(cosH)) / 15.0
        }

        // Sun rise and set zenith = 90.833°
        val sunriseHourAngle = hourAngle(90.833 - 90.0) // Zenith parameter adjustment
        val fajrHourAngle = hourAngle(fajrAngle)
        val asrZenith = Math.toDegrees(atan(1.0 + abs(tan(Math.toRadians(latitude - declination)))))
        val asrHourAngle = hourAngle(90.0 - asrZenith)

        val fajrTimeHours = (noon - fajrHourAngle).coerceIn(4.0, 7.0)
        val dhuhrTimeHours = noon.coerceIn(11.5, 13.5)
        val asrTimeHours = (noon + asrHourAngle).coerceIn(14.0, 18.0)
        val maghribTimeHours = (noon + sunriseHourAngle).coerceIn(16.5, 20.5)
        val ishaTimeHours = if (method == CalculationMethod.UMM_AL_QURA) {
            maghribTimeHours + 1.5 // 90 minutes
        } else {
            val ishaHourAngle = hourAngle(ishaAngle)
            (noon + ishaHourAngle).coerceIn(19.0, 22.5)
        }

        fun formatHours(hours: Double): String {
            val totalMinutes = (hours * 60).roundToInt()
            val h = (totalMinutes / 60) % 24
            val m = totalMinutes % 60
            val amPm = if (h >= 12) "PM" else "AM"
            val displayHour = when {
                h == 0 -> 12
                h > 12 -> h - 12
                else -> h
            }
            return String.format(Locale.US, "%d:%02d %s", displayHour, m, amPm)
        }

        fun addMinutes(hours: Double, mins: Int): String {
            val totalMinutes = (hours * 60).roundToInt() + mins
            val h = (totalMinutes / 60) % 24
            val m = totalMinutes % 60
            val amPm = if (h >= 12) "PM" else "AM"
            val displayHour = when {
                h == 0 -> 12
                h > 12 -> h - 12
                else -> h
            }
            return String.format(Locale.US, "%d:%02d %s", displayHour, m, amPm)
        }

        val fajrAdhan = formatHours(fajrTimeHours)
        val fajrIqamah = addMinutes(fajrTimeHours, 25)
        val dhuhrAdhan = formatHours(dhuhrTimeHours)
        val dhuhrIqamah = addMinutes(dhuhrTimeHours, 20)
        val asrAdhan = formatHours(asrTimeHours)
        val asrIqamah = addMinutes(asrTimeHours, 15)
        val maghribAdhan = formatHours(maghribTimeHours)
        val maghribIqamah = addMinutes(maghribTimeHours, 7)
        val ishaAdhan = formatHours(ishaTimeHours)
        val ishaIqamah = addMinutes(ishaTimeHours, 15)

        val prayerTimes = PrayerTimes(
            fajrAdhan = fajrAdhan,
            fajrIqamah = fajrIqamah,
            dhuhrAdhan = dhuhrAdhan,
            dhuhrIqamah = dhuhrIqamah,
            asrAdhan = asrAdhan,
            asrIqamah = asrIqamah,
            maghribAdhan = maghribAdhan,
            maghribIqamah = maghribIqamah,
            ishaAdhan = ishaAdhan,
            ishaIqamah = ishaIqamah,
            jummah1Khutbah = "1:15 PM",
            jummah1Prayer = "1:45 PM",
            jummah2Khutbah = "2:15 PM",
            jummah2Prayer = "2:45 PM"
        )

        // Determine current time in minutes of the day
        val currentMinutesOfDay = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        data class PrayerSlot(val name: String, val adhanMin: Int, val iqamahMin: Int, val iqamahStr: String)

        val slots = listOf(
            PrayerSlot("Fajr", (fajrTimeHours * 60).roundToInt(), (fajrTimeHours * 60).roundToInt() + 25, fajrIqamah),
            PrayerSlot("Dhuhr", (dhuhrTimeHours * 60).roundToInt(), (dhuhrTimeHours * 60).roundToInt() + 20, dhuhrIqamah),
            PrayerSlot("Asr", (asrTimeHours * 60).roundToInt(), (asrTimeHours * 60).roundToInt() + 15, asrIqamah),
            PrayerSlot("Maghrib", (maghribTimeHours * 60).roundToInt(), (maghribTimeHours * 60).roundToInt() + 7, maghribIqamah),
            PrayerSlot("Isha", (ishaTimeHours * 60).roundToInt(), (ishaTimeHours * 60).roundToInt() + 15, ishaIqamah)
        )

        // Find the next upcoming prayer based on Iqamah time
        val nextSlot = slots.firstOrNull { it.iqamahMin > currentMinutesOfDay } ?: slots.first()
        val minutesRemaining = if (nextSlot.iqamahMin > currentMinutesOfDay) {
            nextSlot.iqamahMin - currentMinutesOfDay
        } else {
            // After Isha: next is tomorrow's Fajr
            (24 * 60 - currentMinutesOfDay) + nextSlot.iqamahMin
        }

        val scheduleList = slots.map { slot ->
            val isNext = slot.name == nextSlot.name
            val adhanTime = when (slot.name) {
                "Fajr" -> fajrAdhan
                "Dhuhr" -> dhuhrAdhan
                "Asr" -> asrAdhan
                "Maghrib" -> maghribAdhan
                else -> ishaAdhan
            }
            PrayerSchedule(
                prayerName = slot.name,
                adhan = adhanTime,
                iqamah = slot.iqamahStr,
                isNext = isNext
            )
        }

        return PrayerCalculationResult(
            prayerTimes = prayerTimes,
            nextPrayerName = nextSlot.name,
            nextIqamahTime = nextSlot.iqamahStr,
            minutesRemaining = minutesRemaining,
            scheduleList = scheduleList
        )
    }
}
