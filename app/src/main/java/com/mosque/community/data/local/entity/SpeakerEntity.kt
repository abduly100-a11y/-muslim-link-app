package com.mosque.community.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mosque.community.data.models.Speaker

@Entity(tableName = "speakers")
data class SpeakerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val organization: String,
    val bio: String,
    val imageUrl: String,
    val followerCount: Int,
    val isFollowed: Boolean,
    val topics: String,
    val languages: String
) {
    fun toSpeaker(): Speaker = Speaker(
        id = id,
        name = name,
        title = title,
        organization = organization,
        bio = bio,
        imageUrl = imageUrl,
        followerCount = followerCount,
        isFollowed = isFollowed,
        topics = if (topics.isBlank()) emptyList() else topics.split("||"),
        languages = if (languages.isBlank()) emptyList() else languages.split("||")
    )

    companion object {
        fun fromSpeaker(speaker: Speaker): SpeakerEntity = SpeakerEntity(
            id = speaker.id,
            name = speaker.name,
            title = speaker.title,
            organization = speaker.organization,
            bio = speaker.bio,
            imageUrl = speaker.imageUrl,
            followerCount = speaker.followerCount,
            isFollowed = speaker.isFollowed,
            topics = speaker.topics.joinToString("||"),
            languages = speaker.languages.joinToString("||")
        )
    }
}
