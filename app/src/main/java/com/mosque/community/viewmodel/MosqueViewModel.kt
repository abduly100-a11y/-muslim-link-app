package com.mosque.community.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mosque.community.data.models.CalculationMethod
import com.mosque.community.data.models.DistanceRadius
import com.mosque.community.data.models.Event
import com.mosque.community.data.models.EventFilter
import com.mosque.community.data.models.Mosque
import com.mosque.community.data.models.PrayerTimes
import com.mosque.community.data.models.QiblaInfo
import com.mosque.community.data.models.RsvpState
import com.mosque.community.data.models.Speaker
import com.mosque.community.data.models.User
import com.mosque.community.data.models.UserRole
import com.mosque.community.data.repository.MosqueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class MosqueViewModel(
    private val repository: MosqueRepository = MosqueRepository()
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(EventFilter.ALL)
    val selectedFilter: StateFlow<EventFilter> = _selectedFilter.asStateFlow()

    val mosques: StateFlow<List<Mosque>> = repository.mosques
    val speakers: StateFlow<List<Speaker>> = repository.speakers
    val followedCategories: StateFlow<Set<String>> = repository.followedCategories
    val currentUser: StateFlow<User> = repository.currentUser

    private val _selectedRadius = MutableStateFlow(DistanceRadius.ALL)
    val selectedRadius: StateFlow<DistanceRadius> = _selectedRadius.asStateFlow()

    private val _selectedCalculationMethod = MutableStateFlow(CalculationMethod.ISNA)
    val selectedCalculationMethod: StateFlow<CalculationMethod> = _selectedCalculationMethod.asStateFlow()

    private val _qiblaInfo = MutableStateFlow(QiblaInfo())
    val qiblaInfo: StateFlow<QiblaInfo> = _qiblaInfo.asStateFlow()

    val filteredNearbyMosques: StateFlow<List<Mosque>> = combine(
        repository.mosques,
        _selectedRadius
    ) { list, radius ->
        list
            .filter { it.distanceMiles <= radius.maxMiles }
            .sortedBy { it.distanceMiles }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectRadius(radius: DistanceRadius) {
        _selectedRadius.value = radius
    }

    fun selectCalculationMethod(method: CalculationMethod) {
        _selectedCalculationMethod.value = method
    }

    fun updateCompassHeading(newHeading: Float) {
        val diff = Math.abs(newHeading - _qiblaInfo.value.qiblaBearing)
        val aligned = diff < 3.0f || diff > 357.0f
        _qiblaInfo.value = _qiblaInfo.value.copy(
            currentHeading = newHeading,
            isAligned = aligned
        )
    }

    fun loginAs(user: User) {
        repository.loginAs(user)
    }

    fun login(email: String, role: UserRole, mosqueId: String? = null): User {
        return repository.login(email, role, mosqueId)
    }

    fun logout() {
        repository.logout()
    }

    fun updatePrayerTimes(mosqueId: String, newPrayerTimes: PrayerTimes) {
        repository.updatePrayerTimes(mosqueId, newPrayerTimes)
    }

    // Home feed filtered events
    val filteredEvents: StateFlow<List<Event>> = combine(
        repository.events,
        _selectedFilter
    ) { events, filter ->
        when (filter) {
            EventFilter.ALL -> events
            EventFilter.TONIGHT -> events.filter { it.isTonight }
            EventFilter.THIS_WEEK -> events.filter { it.isThisWeek }
            EventFilter.LECTURES -> events.filter { it.category.equals("Lectures", ignoreCase = true) }
            EventFilter.COMMUNITY_IFTAR -> events.filter { it.category.equals("Community iftar", ignoreCase = true) }
            EventFilter.WEEKEND -> events.filter { it.dayOfWeek.equals("SAT", ignoreCase = true) || it.dayOfWeek.equals("SUN", ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timeline of events from followed mosques, followed speakers, or followed categories
    val followingTimeline: StateFlow<Map<String, List<Event>>> = combine(
        repository.events,
        repository.mosques,
        repository.speakers,
        repository.followedCategories
    ) { events, mosques, speakers, categories ->
        val followedMosqueIds = mosques.filter { it.isFollowed }.map { it.id }.toSet()
        val followedSpeakerIds = speakers.filter { it.isFollowed }.map { it.id }.toSet()

        events
            .filter { event ->
                followedMosqueIds.contains(event.mosqueId) ||
                event.speakerIds.any { followedSpeakerIds.contains(it) } ||
                categories.contains(event.category)
            }
            .groupBy { it.dateLabel }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Quick primary mosque for countdown header
    val primaryMosque: StateFlow<Mosque?> = repository.mosques.combine(_selectedFilter) { list, _ ->
        list.firstOrNull { it.isFollowed } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val livePrayerCalculation: StateFlow<com.mosque.community.util.prayer.AstronomicalPrayerCalculator.PrayerCalculationResult> = _selectedCalculationMethod.combine(
        primaryMosque
    ) { method, _ ->
        com.mosque.community.util.prayer.AstronomicalPrayerCalculator.calculatePrayerTimes(method = method)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.mosque.community.util.prayer.AstronomicalPrayerCalculator.calculatePrayerTimes(method = CalculationMethod.ISNA)
    )

    fun updateLocation(latitude: Double, longitude: Double, cityName: String = "Current Location") {
        val bearing = com.mosque.community.util.prayer.AstronomicalPrayerCalculator.calculateQiblaBearing(latitude, longitude)
        val distance = com.mosque.community.util.prayer.AstronomicalPrayerCalculator.calculateDistanceToKaabaMiles(latitude, longitude)
        val diff = Math.abs(_qiblaInfo.value.currentHeading - bearing)
        val aligned = diff < 3.0f || diff > 357.0f
        _qiblaInfo.value = _qiblaInfo.value.copy(
            qiblaBearing = bearing,
            distanceToMakkahMiles = distance,
            city = cityName,
            isAligned = aligned
        )
    }

    fun selectFilter(filter: EventFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFollowMosque(mosqueId: String) {
        repository.toggleFollowMosque(mosqueId)
    }

    fun toggleFollowSpeaker(speakerId: String) {
        repository.toggleFollowSpeaker(speakerId)
    }

    fun toggleFollowCategory(category: String) {
        repository.toggleFollowCategory(category)
    }

    fun setEventRsvp(eventId: String, state: RsvpState) {
        repository.setEventRsvp(eventId, state)
    }

    fun toggleBookmark(eventId: String) {
        repository.toggleBookmark(eventId)
    }

    fun toggleReminder(eventId: String, context: android.content.Context? = null) {
        val event = repository.events.value.firstOrNull { it.id == eventId }
        repository.toggleReminder(eventId)
        if (context != null && event != null) {
            if (!event.reminderEnabled) {
                com.mosque.community.util.notification.NotificationHelper.scheduleEventReminder(
                    context = context,
                    eventId = event.id,
                    eventTitle = event.title,
                    mosqueName = event.mosqueName,
                    eventTimeEpochMillis = event.timestampEpochMillis
                )
            } else {
                com.mosque.community.util.notification.NotificationHelper.cancelEventReminder(context, event.id)
            }
        }
    }

    fun getMosqueById(mosqueId: String): Mosque? {
        return mosques.value.firstOrNull { it.id == mosqueId }
    }

    fun getSpeakerById(speakerId: String): Speaker? {
        return speakers.value.firstOrNull { it.id == speakerId }
    }

    fun getEventById(eventId: String): Event? {
        return repository.events.value.firstOrNull { it.id == eventId }
    }

    fun getEventsByMosque(mosqueId: String): List<Event> {
        return repository.events.value.filter { it.mosqueId == mosqueId }
    }

    fun getEventsBySpeaker(speakerId: String): List<Event> {
        return repository.events.value.filter { it.speakerIds.contains(speakerId) }
    }

    fun createEvent(
        title: String,
        date: String,
        time: String,
        description: String,
        category: String,
        location: String,
        posterUrl: String,
        notifyFollowers: Boolean,
        speakerIds: List<String> = emptyList(),
        speakerNames: List<String> = emptyList(),
        capacity: Int? = null,
        registrationRequired: Boolean = false,
        cost: Double? = null,
        isOnline: Boolean = false,
        tags: List<String> = emptyList()
    ): Boolean {
        val adminUser = currentUser.value
        val defaultMosque = if (adminUser.isMosqueAdmin && adminUser.adminMosqueId != null) {
            mosques.value.firstOrNull { it.id == adminUser.adminMosqueId } ?: mosques.value.firstOrNull()
        } else {
            mosques.value.firstOrNull()
        } ?: return false

        val newEvent = Event(
            id = UUID.randomUUID().toString(),
            mosqueId = defaultMosque.id,
            mosqueName = defaultMosque.name,
            mosqueImageUrl = defaultMosque.imageUrl,
            title = title,
            description = description,
            posterUrl = posterUrl.ifBlank {
                "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?auto=format&fit=crop&w=1200&q=80"
            },
            timestampEpochMillis = System.currentTimeMillis() + 86400000L,
            endTimestampEpochMillis = System.currentTimeMillis() + 86400000L + 5400000L,
            dateLabel = if (date.contains("Today", ignoreCase = true) || date.contains("Tonight", ignoreCase = true)) "Tonight" else date,
            fullDate = date,
            timeLabel = time,
            location = location.ifBlank { "Main Prayer Hall" },
            category = category,
            isTonight = date.contains("Tonight", ignoreCase = true) || date.contains("Today", ignoreCase = true),
            isThisWeek = true,
            isBookmarked = false,
            rsvpState = RsvpState.NOT_ATTENDING,
            reminderEnabled = notifyFollowers,
            attendeeCount = 1,
            capacity = capacity,
            registrationRequired = registrationRequired,
            cost = cost,
            isOnline = isOnline,
            speakerIds = speakerIds,
            speakerNames = speakerNames,
            tags = tags,
            isMosqueFollowed = defaultMosque.isFollowed
        )
        repository.addEvent(newEvent)
        return true
    }
}
