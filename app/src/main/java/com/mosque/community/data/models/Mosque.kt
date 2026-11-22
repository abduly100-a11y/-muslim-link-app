package com.mosque.community.data.models

data class Mosque(
    val id: String,
    val name: String,
    val address: String,
    val cityState: String,
    val distanceMiles: Double,
    val imageUrl: String,
    val followerCount: Int,
    val isFollowed: Boolean = false,
    val prayerTimes: PrayerTimes = PrayerTimes(),
    val description: String,
    val phone: String = "(555) 234-5678",
    val website: String = "www.masjidcommunity.org"
)
