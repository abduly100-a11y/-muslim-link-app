package com.mosque.community.data.remote.dto

import com.mosque.community.data.local.entity.EventEntity
import com.mosque.community.data.local.entity.MosqueEntity
import com.mosque.community.data.local.entity.SpeakerEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MosqueDto(
    val id: String,
    val name: String,
    val address: String = "",
    @SerialName("city_state") val cityState: String = "",
    @SerialName("distance_miles") val distanceMiles: Double = 0.0,
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("follower_count") val followerCount: Int = 0,
    val description: String = "",
    val phone: String = "",
    val website: String = "",
    @SerialName("fajr_adhan") val fajrAdhan: String = "",
    @SerialName("fajr_iqamah") val fajrIqamah: String = "",
    @SerialName("dhuhr_adhan") val dhuhrAdhan: String = "",
    @SerialName("dhuhr_iqamah") val dhuhrIqamah: String = "",
    @SerialName("asr_adhan") val asrAdhan: String = "",
    @SerialName("asr_iqamah") val asrIqamah: String = "",
    @SerialName("maghrib_adhan") val maghribAdhan: String = "",
    @SerialName("maghrib_iqamah") val maghribIqamah: String = "",
    @SerialName("isha_adhan") val ishaAdhan: String = "",
    @SerialName("isha_iqamah") val ishaIqamah: String = "",
    @SerialName("jummah_1_khutbah") val jummah1Khutbah: String = "",
    @SerialName("jummah_1_prayer") val jummah1Prayer: String = "",
    @SerialName("jummah_2_khutbah") val jummah2Khutbah: String = "",
    @SerialName("jummah_2_prayer") val jummah2Prayer: String = ""
) {
    fun toEntity(isFollowed: Boolean = false): MosqueEntity = MosqueEntity(
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
}

@Serializable
data class SpeakerDto(
    val id: String,
    val name: String,
    val title: String = "",
    val bio: String = "",
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("follower_count") val followerCount: Int = 0,
    val topics: List<String> = emptyList(),
    val languages: List<String> = emptyList()
) {
    fun toEntity(isFollowed: Boolean = false): SpeakerEntity = SpeakerEntity(
        id = id,
        name = name,
        title = title,
        bio = bio,
        imageUrl = imageUrl,
        followerCount = followerCount,
        isFollowed = isFollowed,
        topics = topics.joinToString("||"),
        languages = languages.joinToString("||")
    )
}

@Serializable
data class EventDto(
    val id: String,
    @SerialName("mosque_id") val mosqueId: String,
    @SerialName("mosque_name") val mosqueName: String = "",
    @SerialName("mosque_image_url") val mosqueImageUrl: String = "",
    val title: String,
    val description: String = "",
    @SerialName("poster_url") val posterUrl: String = "",
    @SerialName("timestamp_epoch_millis") val timestampEpochMillis: Long = 0L,
    @SerialName("end_timestamp_epoch_millis") val endTimestampEpochMillis: Long = 0L,
    @SerialName("date_month") val dateMonth: String = "",
    @SerialName("date_day") val dateDay: String = "",
    @SerialName("day_of_week") val dayOfWeek: String = "",
    @SerialName("date_label") val dateLabel: String = "",
    @SerialName("full_date") val fullDate: String = "",
    @SerialName("time_label") val timeLabel: String = "",
    val location: String = "",
    val address: String = "",
    val category: String = "",
    @SerialName("is_tonight") val isTonight: Boolean = false,
    @SerialName("is_this_week") val isThisWeek: Boolean = false,
    @SerialName("attendee_count") val attendeeCount: Int = 0,
    val capacity: Int? = null,
    @SerialName("registration_required") val registrationRequired: Boolean = false,
    val cost: Double? = 0.0,
    @SerialName("is_online") val isOnline: Boolean = false,
    val language: String = "English",
    @SerialName("speaker_ids") val speakerIds: List<String> = emptyList(),
    @SerialName("speaker_names") val speakerNames: List<String> = emptyList(),
    val tags: List<String> = emptyList()
) {
    fun toEntity(
        isBookmarked: Boolean = false,
        rsvpState: String = "NOT_ATTENDING",
        reminderEnabled: Boolean = false,
        isMosqueFollowed: Boolean = false
    ): EventEntity = EventEntity(
        id = id,
        mosqueId = mosqueId,
        mosqueName = mosqueName,
        mosqueImageUrl = mosqueImageUrl,
        title = title,
        description = description,
        posterUrl = posterUrl,
        timestampEpochMillis = timestampEpochMillis,
        endTimestampEpochMillis = endTimestampEpochMillis,
        dateMonth = dateMonth,
        dateDay = dateDay,
        dayOfWeek = dayOfWeek,
        dateLabel = dateLabel,
        fullDate = fullDate,
        timeLabel = timeLabel,
        location = location,
        address = address,
        category = category,
        isTonight = isTonight,
        isThisWeek = isThisWeek,
        isBookmarked = isBookmarked,
        rsvpState = rsvpState,
        reminderEnabled = reminderEnabled,
        attendeeCount = attendeeCount,
        capacity = capacity,
        registrationRequired = registrationRequired,
        cost = cost,
        isOnline = isOnline,
        language = language,
        speakerIds = speakerIds.joinToString("||"),
        speakerNames = speakerNames.joinToString("||"),
        tags = tags.joinToString("||"),
        isMosqueFollowed = isMosqueFollowed
    )
}
