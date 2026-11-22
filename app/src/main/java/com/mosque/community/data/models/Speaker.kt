package com.mosque.community.data.models

data class Speaker(
    val id: String,
    val name: String,
    val title: String = "Scholar & Lecturer",
    val bio: String,
    val imageUrl: String,
    val topics: List<String> = emptyList(),
    val languages: List<String> = listOf("English"),
    val followerCount: Int = 0,
    val isFollowed: Boolean = false
)
