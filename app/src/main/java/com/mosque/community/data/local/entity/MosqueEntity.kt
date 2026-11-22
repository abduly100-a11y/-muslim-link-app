package com.mosque.community.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mosque.community.data.models.Mosque
import com.mosque.community.data.models.PrayerTimes

@Entity(tableName = "mosques")
data class MosqueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val cityState: String,
    val distanceMiles: Double,
    val imageUrl: String,
    val followerCount: Int,
    val isFollowed: Boolean,
    val description: String,
    val phone: String,
    val website: String,
    val fajrAdhan: String,
    val fajrIqamah: String,
    val dhuhrAdhan: String,
    val dhuhrIqamah: String,
    val asrAdhan: String,
    val asrIqamah: String,
    val maghribAdhan: String,
    val maghribIqamah: String,
    val ishaAdhan: String,
    val ishaIqamah: String,
    val jummah1Khutbah: String,
    val jummah1Prayer: String,
    val jummah2Khutbah: String,
    val jummah2Prayer: String
) {
    fun toMosque(): Mosque = Mosque(
        id = id,
        name = name,
        address = address,
        cityState = cityState,
        distanceMiles = distanceMiles,
        imageUrl = imageUrl,
        followerCount = followerCount,
        isFollowed = isFollowed,
        description = description,
        phone = phone,
        website = website,
        prayerTimes = PrayerTimes(
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
            jummah1Khutbah = jummah1Khutbah,
            jummah1Prayer = jummah1Prayer,
            jummah2Khutbah = jummah2Khutbah,
            jummah2Prayer = jummah2Prayer
        )
    )

    companion object {
        fun fromMosque(mosque: Mosque): MosqueEntity = MosqueEntity(
            id = mosque.id,
            name = mosque.name,
            address = mosque.address,
            cityState = mosque.cityState,
            distanceMiles = mosque.distanceMiles,
            imageUrl = mosque.imageUrl,
            followerCount = mosque.followerCount,
            isFollowed = mosque.isFollowed,
            description = mosque.description,
            phone = mosque.phone,
            website = mosque.website,
            fajrAdhan = mosque.prayerTimes.fajrAdhan,
            fajrIqamah = mosque.prayerTimes.fajrIqamah,
            dhuhrAdhan = mosque.prayerTimes.dhuhrAdhan,
            dhuhrIqamah = mosque.prayerTimes.dhuhrIqamah,
            asrAdhan = mosque.prayerTimes.asrAdhan,
            asrIqamah = mosque.prayerTimes.asrIqamah,
            maghribAdhan = mosque.prayerTimes.maghribAdhan,
            maghribIqamah = mosque.prayerTimes.maghribIqamah,
            ishaAdhan = mosque.prayerTimes.ishaAdhan,
            ishaIqamah = mosque.prayerTimes.ishaIqamah,
            jummah1Khutbah = mosque.prayerTimes.jummah1Khutbah,
            jummah1Prayer = mosque.prayerTimes.jummah1Prayer,
            jummah2Khutbah = mosque.prayerTimes.jummah2Khutbah,
            jummah2Prayer = mosque.prayerTimes.jummah2Prayer
        )
    }
}
