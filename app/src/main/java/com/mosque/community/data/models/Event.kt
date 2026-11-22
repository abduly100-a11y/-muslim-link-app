package com.mosque.community.data.models

enum class EventFilter(val label: String) {
    ALL("All"),
    TONIGHT("Tonight"),
    THIS_WEEK("This week"),
    LECTURES("Lectures"),
    COMMUNITY_IFTAR("Community iftar"),
    WEEKEND("This weekend")
}

data class Event(
    val id: String,
    val mosqueId: String,
    val mosqueName: String,
    val mosqueImageUrl: String,
    val title: String,
    val description: String,
    val posterUrl: String,
    val timestampEpochMillis: Long = 1732046400000L,
    val endTimestampEpochMillis: Long = 1732051800000L,
    val dateMonth: String = "NOV", // Luma-style Date Badge
    val dateDay: String = "19",
    val dayOfWeek: String = "FRI",
    val dateLabel: String = "Tonight",
    val fullDate: String = "Friday, Nov 19",
    val timeLabel: String = "8:00 PM - 9:30 PM",
    val location: String = "Main Prayer Hall",
    val address: String = "",
    val category: String = "Lectures",
    val isTonight: Boolean = false,
    val isThisWeek: Boolean = true,
    val isBookmarked: Boolean = false, // Backwards-compatible RSVP flag
    val rsvpState: RsvpState = if (isBookmarked) RsvpState.GOING else RsvpState.NOT_ATTENDING,
    val reminderEnabled: Boolean = false,
    val attendeeCount: Int = 48,
    val capacity: Int? = null,
    val registrationRequired: Boolean = false,
    val cost: Double? = null, // null or 0.0 = Free
    val isOnline: Boolean = false,
    val language: String = "English",
    val speakerIds: List<String> = emptyList(),
    val speakerNames: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val isMosqueFollowed: Boolean = false
) {
    val isFree: Boolean get() = cost == null || cost == 0.0
    val isAtCapacity: Boolean get() = capacity != null && attendeeCount >= capacity
    val shareUrl: String get() = "https://muslimlink.app/event/$id"
    val shareText: String get() = "Join me for '$title' at $mosqueName on $fullDate ($timeLabel)! RSVP & details: $shareUrl"
}
