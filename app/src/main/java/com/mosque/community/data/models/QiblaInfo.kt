package com.mosque.community.data.models

enum class CalculationMethod(val title: String, val description: String) {
    ISNA("ISNA", "Islamic Society of North America (Fajr 15°, Isha 15°)"),
    UMM_AL_QURA("Umm al-Qura", "Makkah, Saudi Arabia (Fajr 18.5°, Isha 90 min)"),
    MWL("Muslim World League", "Europe & International (Fajr 18°, Isha 17°)"),
    KARACHI("Karachi", "Univ. of Islamic Sciences (Fajr 18°, Isha 18°)")
}

enum class DistanceRadius(val label: String, val maxMiles: Double) {
    ALL("All", Double.MAX_VALUE),
    ONE_MILE("1 mi", 1.0),
    FIVE_MILES("5 mi", 5.0),
    TEN_MILES("10 mi", 10.0)
}

data class QiblaInfo(
    val qiblaBearing: Float = 58.4f, // Bearing from user's location to Makkah in degrees
    val currentHeading: Float = 58.4f,
    val distanceToMakkahMiles: Int = 6840,
    val city: String = "Springfield, IL",
    val isAligned: Boolean = true
)
