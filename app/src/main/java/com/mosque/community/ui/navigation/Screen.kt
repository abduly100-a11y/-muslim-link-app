package com.mosque.community.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Discover : Screen(
        route = "discover",
        title = "Discover",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    )

    object Mosques : Screen(
        route = "mosques",
        title = "Mosques",
        selectedIcon = Icons.Filled.LocationCity,
        unselectedIcon = Icons.Outlined.LocationCity
    )

    object Speakers : Screen(
        route = "speakers",
        title = "Speakers",
        selectedIcon = Icons.Filled.RecordVoiceOver,
        unselectedIcon = Icons.Outlined.RecordVoiceOver
    )

    object MySchedule : Screen(
        route = "my_schedule",
        title = "My Schedule",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    )

    object Profile : Screen(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    // Backward compatibility alias for Communities -> Mosques
    val Communities get() = Mosques

    companion object {
        val bottomNavItems = listOf(Discover, Mosques, Speakers, MySchedule, Profile)
        const val MOSQUE_PROFILE_ROUTE = "mosque_profile/{mosqueId}"
        const val SPEAKER_PROFILE_ROUTE = "speaker_profile/{speakerId}"
        const val EVENT_DETAIL_ROUTE = "event/{eventId}"
        const val POST_EVENT_ROUTE = "post_event"
        const val QIBLA_ROUTE = "qibla"

        fun mosqueProfile(mosqueId: String) = "mosque_profile/$mosqueId"
        fun speakerProfile(speakerId: String) = "speaker_profile/$speakerId"
        fun eventDetail(eventId: String) = "event/$eventId"
    }
}
