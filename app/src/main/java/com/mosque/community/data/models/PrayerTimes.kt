package com.mosque.community.data.models

data class PrayerSchedule(
    val prayerName: String,
    val adhan: String,
    val iqamah: String,
    val isNext: Boolean = false
)

data class PrayerTimes(
    val fajrAdhan: String = "5:15 AM",
    val fajrIqamah: String = "5:45 AM",
    val dhuhrAdhan: String = "1:05 PM",
    val dhuhrIqamah: String = "1:30 PM",
    val asrAdhan: String = "4:45 PM",
    val asrIqamah: String = "5:00 PM",
    val maghribAdhan: String = "7:48 PM",
    val maghribIqamah: String = "7:53 PM",
    val ishaAdhan: String = "9:15 PM",
    val ishaIqamah: String = "9:30 PM",
    val jummah1Khutbah: String = "1:15 PM",
    val jummah1Prayer: String = "1:45 PM",
    val jummah2Khutbah: String = "2:15 PM",
    val jummah2Prayer: String = "2:45 PM"
) {
    fun toScheduleList(): List<PrayerSchedule> = listOf(
        PrayerSchedule("Fajr", fajrAdhan, fajrIqamah),
        PrayerSchedule("Dhuhr", dhuhrAdhan, dhuhrIqamah),
        PrayerSchedule("Asr", asrAdhan, asrIqamah, isNext = true),
        PrayerSchedule("Maghrib", maghribAdhan, maghribIqamah),
        PrayerSchedule("Isha", ishaAdhan, ishaIqamah)
    )
}
