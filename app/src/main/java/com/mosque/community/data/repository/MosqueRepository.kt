package com.mosque.community.data.repository

import com.mosque.community.data.local.MosqueDatabase
import com.mosque.community.data.local.entity.EventEntity
import com.mosque.community.data.local.entity.MosqueEntity
import com.mosque.community.data.local.entity.SpeakerEntity
import com.mosque.community.data.models.Event
import com.mosque.community.data.models.Mosque
import com.mosque.community.data.models.PrayerTimes
import com.mosque.community.data.models.RsvpState
import com.mosque.community.data.models.Speaker
import com.mosque.community.data.models.User
import com.mosque.community.data.models.UserRole
import com.mosque.community.data.remote.SupabaseService
import com.mosque.community.data.remote.dto.EventDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MosqueRepository(
    private val explicitContext: android.content.Context? = null,
    private val supabaseService: SupabaseService = SupabaseService()
) {
    companion object {
        private var globalContext: android.content.Context? = null

        fun initialize(context: android.content.Context) {
            globalContext = context.applicationContext
        }

        @Volatile
        private var INSTANCE: MosqueRepository? = null

        fun getInstance(context: android.content.Context? = null): MosqueRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MosqueRepository(context?.applicationContext ?: globalContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private val effectiveContext: android.content.Context?
        get() = explicitContext ?: globalContext

    private val database: MosqueDatabase? by lazy {
        effectiveContext?.let { MosqueDatabase.getDatabase(it) }
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val prefs: android.content.SharedPreferences?
        get() = effectiveContext?.getSharedPreferences("muslim_link_prefs", android.content.Context.MODE_PRIVATE)

    private val initialMosques = listOf(
        Mosque(
            id = "m1",
            name = "Al-Noor Islamic Cultural Center",
            address = "742 Evergreen Terrace",
            cityState = "Springfield, IL",
            distanceMiles = 0.8,
            imageUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?auto=format&fit=crop&w=1200&q=80",
            followerCount = 1420,
            isFollowed = true,
            prayerTimes = PrayerTimes(
                fajrAdhan = "5:15 AM", fajrIqamah = "5:45 AM",
                dhuhrAdhan = "1:05 PM", dhuhrIqamah = "1:30 PM",
                asrAdhan = "4:45 PM", asrIqamah = "5:00 PM",
                maghribAdhan = "7:48 PM", maghribIqamah = "7:55 PM",
                ishaAdhan = "9:15 PM", ishaIqamah = "9:30 PM",
                jummah1Khutbah = "1:15 PM", jummah1Prayer = "1:45 PM",
                jummah2Khutbah = "2:15 PM", jummah2Prayer = "2:45 PM"
            ),
            description = "A welcoming center for community worship, educational lectures, youth sports, and family programs located in the heart of Springfield."
        ),
        Mosque(
            id = "m2",
            name = "Medina Community Masjid",
            address = "1204 Crescent Blvd",
            cityState = "Oakwood, IL",
            distanceMiles = 2.4,
            imageUrl = "https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=1200&q=80",
            followerCount = 890,
            isFollowed = false,
            prayerTimes = PrayerTimes(
                fajrAdhan = "5:18 AM", fajrIqamah = "5:45 AM",
                dhuhrAdhan = "1:05 PM", dhuhrIqamah = "1:25 PM",
                asrAdhan = "4:48 PM", asrIqamah = "5:05 PM",
                maghribAdhan = "7:48 PM", maghribIqamah = "7:53 PM",
                ishaAdhan = "9:15 PM", ishaIqamah = "9:30 PM",
                jummah1Khutbah = "1:20 PM", jummah1Prayer = "1:50 PM"
            ),
            description = "Dedicated to spiritual growth, daily prayers in congregation, sisters' circles, and charitable food pantry distribution."
        ),
        Mosque(
            id = "m3",
            name = "Baitul Mukarram Center",
            address = "385 Sunrise Highway",
            cityState = "Westfield, IL",
            distanceMiles = 4.1,
            imageUrl = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?auto=format&fit=crop&w=1200&q=80",
            followerCount = 2150,
            isFollowed = true,
            prayerTimes = PrayerTimes(
                fajrAdhan = "5:12 AM", fajrIqamah = "5:40 AM",
                dhuhrAdhan = "1:05 PM", dhuhrIqamah = "1:30 PM",
                asrAdhan = "4:42 PM", asrIqamah = "5:00 PM",
                maghribAdhan = "7:48 PM", maghribIqamah = "7:54 PM",
                ishaAdhan = "9:15 PM", ishaIqamah = "9:30 PM",
                jummah1Khutbah = "1:00 PM", jummah1Prayer = "1:30 PM",
                jummah2Khutbah = "2:00 PM", jummah2Prayer = "2:30 PM"
            ),
            description = "Historic mosque featuring classical Ottoman and Andalusian design elements, full-time Hifz school, and vibrant community hall."
        ),
        Mosque(
            id = "m4",
            name = "Valley Islamic Society",
            address = "890 Riverdale Road",
            cityState = "Riverdale, IL",
            distanceMiles = 6.0,
            imageUrl = "https://images.unsplash.com/photo-1591604129939-f1efa4d9f7fa?auto=format&fit=crop&w=1200&q=80",
            followerCount = 630,
            isFollowed = false,
            prayerTimes = PrayerTimes(
                fajrAdhan = "5:15 AM", fajrIqamah = "5:50 AM",
                dhuhrAdhan = "1:05 PM", dhuhrIqamah = "1:30 PM",
                asrAdhan = "4:45 PM", asrIqamah = "5:10 PM",
                maghribAdhan = "7:48 PM", maghribIqamah = "7:55 PM",
                ishaAdhan = "9:15 PM", ishaIqamah = "9:30 PM"
            ),
            description = "Serving south valley families with engaging youth halaqahs, family picnics, and weekend Islamic school."
        )
    )

    private val initialSpeakers = listOf(
        Speaker(
            id = "s1",
            name = "Dr. Tariq Rahman",
            title = "Islamic Scholar & Author",
            bio = "Dr. Tariq Rahman specializes in Quranic Sciences and contemporary spiritual ethics. He holds degrees in Islamic Jurisprudence and Psychology and lectures internationally on faith and modern living.",
            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=600&q=80",
            topics = listOf("Tafseer", "Spiritual Growth", "Ethics", "Youth"),
            languages = listOf("English", "Arabic"),
            followerCount = 1420,
            isFollowed = true
        ),
        Speaker(
            id = "s2",
            name = "Sheikh Bilal Philips",
            title = "Dean & Islamic Educator",
            bio = "Sheikh Bilal Philips is a renowned scholar and educator who has pioneered accessible Islamic education worldwide. He regularly conducts youth mentorship and family workshops.",
            imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=600&q=80",
            topics = listOf("Aqeedah", "Comparative Religion", "Youth Development"),
            languages = listOf("English"),
            followerCount = 3850,
            isFollowed = false
        ),
        Speaker(
            id = "s3",
            name = "Ustadha Fatima Barkatullah",
            title = "Islamic Scholar & Podcaster",
            bio = "Ustadha Fatima is an esteemed British scholar, author, and researcher in Islamic Family Law. She conducts sisters circles, family counseling, and community lectures.",
            imageUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=600&q=80",
            topics = listOf("Sisters Halaqa", "Family", "Fiqh of Women", "Quranic Reflection"),
            languages = listOf("English", "Arabic"),
            followerCount = 2190,
            isFollowed = true
        ),
        Speaker(
            id = "s4",
            name = "Imam Omar Suleiman",
            title = "President of Yaqeen Institute",
            bio = "Imam Omar Suleiman is an internationally celebrated scholar, social justice activist, and founder of Yaqeen Institute for Islamic Research.",
            imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=600&q=80",
            topics = listOf("Seerah", "Social Justice", "Spiritual Awakening"),
            languages = listOf("English", "Arabic"),
            followerCount = 6500,
            isFollowed = false
        )
    )

    private val initialEvents = listOf(
        Event(
            id = "e1",
            mosqueId = "m1",
            mosqueName = "Masjid Al-Noor",
            mosqueImageUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?auto=format&fit=crop&w=400&q=80",
            title = "Finding Peace in Trials & Tribulations",
            description = "Join us for an uplifting evening lecture exploring prophetic resilience and inner calm with guest speaker Dr. Tariq Rahman.",
            posterUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?auto=format&fit=crop&w=1200&q=80",
            dateMonth = "NOV",
            dateDay = "19",
            dayOfWeek = "FRI",
            dateLabel = "Tonight",
            fullDate = "Friday, Nov 19",
            timeLabel = "8:00 PM - 9:30 PM",
            location = "Main Prayer Hall",
            address = "742 Evergreen Terrace, Springfield, IL",
            category = "Lectures",
            isTonight = true,
            isThisWeek = true,
            isBookmarked = true,
            rsvpState = RsvpState.GOING,
            reminderEnabled = true,
            attendeeCount = 48,
            capacity = 120,
            registrationRequired = false,
            cost = 0.0,
            isOnline = false,
            language = "English",
            speakerIds = listOf("s1"),
            speakerNames = listOf("Dr. Tariq Rahman"),
            tags = listOf("Spirituality", "Faith", "Youth"),
            isMosqueFollowed = true
        ),
        Event(
            id = "e2",
            mosqueId = "m2",
            mosqueName = "Islamic Center",
            mosqueImageUrl = "https://images.unsplash.com/photo-1564769625905-50e93615e769?auto=format&fit=crop&w=400&q=80",
            title = "Youth Halaqa Night & Open Discussion",
            description = "High school and college youth discussion addressing faith in modern life, followed by tea and refreshments.",
            posterUrl = "https://images.unsplash.com/photo-1519817650390-64a93db51149?auto=format&fit=crop&w=1200&q=80",
            dateMonth = "NOV",
            dateDay = "20",
            dayOfWeek = "SAT",
            dateLabel = "Tomorrow",
            fullDate = "Saturday, Nov 20",
            timeLabel = "7:30 PM - 9:00 PM",
            location = "Youth Multi-Purpose Room",
            address = "1204 Crescent Blvd, Oakwood, IL",
            category = "Youth Program",
            isTonight = false,
            isThisWeek = true,
            isBookmarked = false,
            rsvpState = RsvpState.NOT_ATTENDING,
            reminderEnabled = false,
            attendeeCount = 64,
            capacity = 80,
            registrationRequired = true,
            cost = 0.0,
            isOnline = false,
            language = "English",
            speakerIds = listOf("s2"),
            speakerNames = listOf("Sheikh Bilal Philips"),
            tags = listOf("Youth", "Discussion", "Faith"),
            isMosqueFollowed = false
        ),
        Event(
            id = "e3",
            mosqueId = "m1",
            mosqueName = "Masjid Al-Noor",
            mosqueImageUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?auto=format&fit=crop&w=400&q=80",
            title = "Annual Community Grand Iftar & Gathering",
            description = "Communal fast-breaking dinner featuring home-style meals, community reflection, and congregational Maghrib prayer.",
            posterUrl = "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=1200&q=80",
            dateMonth = "NOV",
            dateDay = "22",
            dayOfWeek = "MON",
            dateLabel = "Monday",
            fullDate = "Monday, Nov 22",
            timeLabel = "6:30 PM - 8:45 PM",
            location = "Main Banquet Hall & Patio",
            address = "742 Evergreen Terrace, Springfield, IL",
            category = "Community iftar",
            isTonight = false,
            isThisWeek = true,
            isBookmarked = false,
            rsvpState = RsvpState.NOT_ATTENDING,
            reminderEnabled = false,
            attendeeCount = 120,
            capacity = 250,
            registrationRequired = true,
            cost = 0.0,
            isOnline = false,
            language = "English",
            speakerIds = emptyList(),
            speakerNames = listOf("Community Imams"),
            tags = listOf("Iftar", "Dinner", "Family"),
            isMosqueFollowed = true
        ),
        Event(
            id = "e4",
            mosqueId = "m3",
            mosqueName = "Baitul Mukarram Center",
            mosqueImageUrl = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?auto=format&fit=crop&w=400&q=80",
            title = "Sisters Quran Circle & Reflection",
            description = "An uplifting weekly gathering dedicated to reciting, understanding, and living the verses of the Holy Quran.",
            posterUrl = "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?auto=format&fit=crop&w=1200&q=80",
            dateMonth = "NOV",
            dateDay = "24",
            dayOfWeek = "WED",
            dateLabel = "Wednesday",
            fullDate = "Wednesday, Nov 24",
            timeLabel = "10:30 AM - 12:00 PM",
            location = "Sisters Conference Suite",
            address = "385 Sunrise Highway, Westfield, IL",
            category = "Sisters Program",
            isTonight = false,
            isThisWeek = true,
            isBookmarked = false,
            rsvpState = RsvpState.NOT_ATTENDING,
            reminderEnabled = false,
            attendeeCount = 35,
            capacity = 60,
            registrationRequired = false,
            cost = 0.0,
            isOnline = false,
            language = "English",
            speakerIds = listOf("s3"),
            speakerNames = listOf("Ustadha Fatima Barkatullah"),
            tags = listOf("Sisters", "Quran", "Reflection"),
            isMosqueFollowed = true
        )
    )

    private val _mosques = MutableStateFlow(getInitialMosques())
    val mosques: StateFlow<List<Mosque>> = _mosques.asStateFlow()

    private val _speakers = MutableStateFlow(getInitialSpeakers())
    val speakers: StateFlow<List<Speaker>> = _speakers.asStateFlow()

    private val _followedCategories = MutableStateFlow(getInitialCategories())
    val followedCategories: StateFlow<Set<String>> = _followedCategories.asStateFlow()

    private val _events = MutableStateFlow(getInitialEvents())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    init {
        // Seed Room Database asynchronously if it's empty
        effectiveContext?.let {
            repositoryScope.launch {
                try {
                    val db = database ?: return@launch
                    if (db.mosqueDao().getCount() == 0) {
                        db.mosqueDao().insertAll(_mosques.value.map { MosqueEntity.fromMosque(it) })
                    }
                    if (db.speakerDao().getCount() == 0) {
                        db.speakerDao().insertAll(_speakers.value.map { SpeakerEntity.fromSpeaker(it) })
                    }
                    if (db.eventDao().getCount() == 0) {
                        db.eventDao().insertAll(_events.value.map { EventEntity.fromEvent(it) })
                    }
                } catch (e: Throwable) {
                    // Ignored in test environments or database failures
                }
            }
        }
    }

    suspend fun syncWithSupabase() {
        try {
            // 1. Fetch remote data from Supabase
            val remoteMosques = supabaseService.getMosques()
            val remoteSpeakers = supabaseService.getSpeakers()
            val remoteEvents = supabaseService.getEvents()

            // 2. Update local Room database and StateFlows if remote has records
            val db = database
            if (remoteMosques.isNotEmpty()) {
                val followedIds = _mosques.value.filter { it.isFollowed }.map { it.id }.toSet()
                val entities = remoteMosques.map { it.toEntity(isFollowed = followedIds.contains(it.id)) }
                db?.mosqueDao()?.insertAll(entities)
                _mosques.value = entities.map { it.toMosque() }
            }

            if (remoteSpeakers.isNotEmpty()) {
                val followedIds = _speakers.value.filter { it.isFollowed }.map { it.id }.toSet()
                val entities = remoteSpeakers.map { it.toEntity(isFollowed = followedIds.contains(it.id)) }
                db?.speakerDao()?.insertAll(entities)
                _speakers.value = entities.map { it.toSpeaker() }
            }

            if (remoteEvents.isNotEmpty()) {
                val currentEventsMap = _events.value.associateBy { it.id }
                val entities = remoteEvents.map { dto ->
                    val existing = currentEventsMap[dto.id]
                    dto.toEntity(
                        isBookmarked = existing?.isBookmarked ?: false,
                        rsvpState = existing?.rsvpState?.name ?: "NOT_ATTENDING",
                        reminderEnabled = existing?.reminderEnabled ?: false,
                        isMosqueFollowed = existing?.isMosqueFollowed ?: false
                    )
                }
                db?.eventDao()?.insertAll(entities)
                _events.value = entities.map { it.toEvent() }
            }
        } catch (e: Throwable) {
            // Fail safely to retain offline cache
        }
    }

    private fun getInitialEvents(): List<Event> {
        val p = prefs ?: return initialEvents
        val savedRsvps = p.all.filterKeys { it.startsWith("rsvp_") }
        val savedReminders = p.all.filterKeys { it.startsWith("remind_") }

        return initialEvents.map { event ->
            val savedStateStr = savedRsvps["rsvp_${event.id}"] as? String
            val savedRsvp = if (savedStateStr != null) {
                try { RsvpState.valueOf(savedStateStr) } catch (e: Exception) { event.rsvpState }
            } else event.rsvpState
            val savedRemind = savedReminders["remind_${event.id}"] as? Boolean ?: event.reminderEnabled
            val isBookmarked = savedRsvp == RsvpState.GOING || savedRsvp == RsvpState.INTERESTED
            val delta = if (savedRsvp == RsvpState.GOING && event.rsvpState != RsvpState.GOING) 1
            else if (savedRsvp != RsvpState.GOING && event.rsvpState == RsvpState.GOING) -1 else 0

            event.copy(
                rsvpState = savedRsvp,
                isBookmarked = isBookmarked,
                reminderEnabled = savedRemind,
                attendeeCount = (event.attendeeCount + delta).coerceAtLeast(0)
            )
        }
    }

    private fun getInitialMosques(): List<Mosque> {
        val saved = prefs?.getStringSet("followed_mosques", null) ?: return initialMosques
        return initialMosques.map { mosque ->
            val isFollowed = saved.contains(mosque.id)
            if (isFollowed != mosque.isFollowed) {
                val delta = if (isFollowed) 1 else -1
                mosque.copy(
                    isFollowed = isFollowed,
                    followerCount = (mosque.followerCount + delta).coerceAtLeast(0)
                )
            } else mosque
        }
    }

    private fun getInitialSpeakers(): List<Speaker> {
        val saved = prefs?.getStringSet("followed_speakers", null) ?: return initialSpeakers
        return initialSpeakers.map { speaker ->
            val isFollowed = saved.contains(speaker.id)
            if (isFollowed != speaker.isFollowed) {
                val delta = if (isFollowed) 1 else -1
                speaker.copy(
                    isFollowed = isFollowed,
                    followerCount = (speaker.followerCount + delta).coerceAtLeast(0)
                )
            } else speaker
        }
    }

    private fun getInitialCategories(): Set<String> {
        return prefs?.getStringSet("followed_categories", null) ?: setOf("Lectures", "Youth Program", "Ramadan Program")
    }

    private val _currentUser = MutableStateFlow<User>(User.Guest)
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun loginAs(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = User.Guest
    }

    fun login(email: String, role: UserRole, mosqueId: String? = null): User {
        val user = when (role) {
            UserRole.MOSQUE_ADMIN -> {
                val targetMosque = _mosques.value.firstOrNull { it.id == mosqueId } ?: _mosques.value.first()
                User(
                    id = "admin_" + System.currentTimeMillis(),
                    name = "Imam / Admin (" + targetMosque.name.take(15) + "…)",
                    email = email.ifBlank { "admin@alnoorcenter.org" },
                    role = UserRole.MOSQUE_ADMIN,
                    adminMosqueId = targetMosque.id,
                    adminMosqueName = targetMosque.name
                )
            }
            UserRole.CONGREGANT -> {
                User(
                    id = "user_" + System.currentTimeMillis(),
                    name = email.substringBefore("@").replace(".", " ").capitalize(),
                    email = email,
                    role = UserRole.CONGREGANT
                )
            }
            UserRole.GUEST -> User.Guest
        }
        _currentUser.value = user
        return user
    }

    fun updatePrayerTimes(mosqueId: String, newPrayerTimes: PrayerTimes) {
        _mosques.update { list ->
            list.map { mosque ->
                if (mosque.id == mosqueId) {
                    val updated = mosque.copy(prayerTimes = newPrayerTimes)
                    repositoryScope.launch {
                        database?.mosqueDao()?.update(MosqueEntity.fromMosque(updated))
                    }
                    updated
                } else mosque
            }
        }
    }

    fun toggleFollowMosque(mosqueId: String) {
        var newFollowState = false
        var updatedMosque: Mosque? = null

        _mosques.update { list ->
            list.map { mosque ->
                if (mosque.id == mosqueId) {
                    val nextFollowed = !mosque.isFollowed
                    newFollowState = nextFollowed
                    val countDelta = if (nextFollowed) 1 else -1
                    val modified = mosque.copy(
                        isFollowed = nextFollowed,
                        followerCount = (mosque.followerCount + countDelta).coerceAtLeast(0)
                    )
                    updatedMosque = modified
                    modified
                } else mosque
            }
        }

        // Also synchronize event follow statuses
        _events.update { list ->
            list.map { event ->
                if (event.mosqueId == mosqueId) {
                    event.copy(isMosqueFollowed = newFollowState)
                } else event
            }
        }

        repositoryScope.launch {
            updatedMosque?.let {
                database?.mosqueDao()?.updateFollowStatus(it.id, it.isFollowed, it.followerCount)
                database?.eventDao()?.updateMosqueFollowStatus(it.id, it.isFollowed)
            }
        }

        prefs?.edit()?.putStringSet("followed_mosques", _mosques.value.filter { it.isFollowed }.map { it.id }.toSet())?.apply()
    }

    fun toggleFollowSpeaker(speakerId: String) {
        var updatedSpeaker: Speaker? = null
        _speakers.update { list ->
            list.map { speaker ->
                if (speaker.id == speakerId) {
                    val next = !speaker.isFollowed
                    val delta = if (next) 1 else -1
                    val modified = speaker.copy(
                        isFollowed = next,
                        followerCount = (speaker.followerCount + delta).coerceAtLeast(0)
                    )
                    updatedSpeaker = modified
                    modified
                } else speaker
            }
        }

        repositoryScope.launch {
            updatedSpeaker?.let {
                database?.speakerDao()?.updateFollowStatus(it.id, it.isFollowed, it.followerCount)
            }
        }

        prefs?.edit()?.putStringSet("followed_speakers", _speakers.value.filter { it.isFollowed }.map { it.id }.toSet())?.apply()
    }

    fun toggleFollowCategory(category: String) {
        _followedCategories.update { set ->
            if (set.contains(category)) set - category else set + category
        }
        prefs?.edit()?.putStringSet("followed_categories", _followedCategories.value)?.apply()
    }

    fun setEventRsvp(eventId: String, state: RsvpState) {
        var updatedEvent: Event? = null
        _events.update { list ->
            list.map { event ->
                if (event.id == eventId) {
                    val wasGoing = event.rsvpState == RsvpState.GOING
                    val isNowGoing = state == RsvpState.GOING
                    val delta = when {
                        !wasGoing && isNowGoing -> 1
                        wasGoing && !isNowGoing -> -1
                        else -> 0
                    }
                    val modified = event.copy(
                        rsvpState = state,
                        isBookmarked = state == RsvpState.GOING || state == RsvpState.INTERESTED,
                        attendeeCount = (event.attendeeCount + delta).coerceAtLeast(0)
                    )
                    updatedEvent = modified
                    modified
                } else event
            }
        }

        repositoryScope.launch {
            updatedEvent?.let {
                database?.eventDao()?.updateRsvp(it.id, it.rsvpState.name, it.isBookmarked, it.attendeeCount)
            }
        }

        prefs?.edit()?.putString("rsvp_$eventId", state.name)?.apply()
    }

    fun toggleBookmark(eventId: String) {
        var nextState = RsvpState.NOT_ATTENDING
        var updatedEvent: Event? = null
        _events.update { list ->
            list.map { event ->
                if (event.id == eventId) {
                    val nextGoing = !event.isBookmarked
                    nextState = if (nextGoing) RsvpState.GOING else RsvpState.NOT_ATTENDING
                    val delta = if (nextGoing) 1 else -1
                    val modified = event.copy(
                        isBookmarked = nextGoing,
                        rsvpState = nextState,
                        attendeeCount = (event.attendeeCount + delta).coerceAtLeast(0)
                    )
                    updatedEvent = modified
                    modified
                } else event
            }
        }

        repositoryScope.launch {
            updatedEvent?.let {
                database?.eventDao()?.updateRsvp(it.id, it.rsvpState.name, it.isBookmarked, it.attendeeCount)
            }
        }

        prefs?.edit()?.putString("rsvp_$eventId", nextState.name)?.apply()
    }

    fun toggleReminder(eventId: String) {
        var nextRemind = false
        var updatedEvent: Event? = null
        _events.update { list ->
            list.map { event ->
                if (event.id == eventId) {
                    nextRemind = !event.reminderEnabled
                    val modified = event.copy(reminderEnabled = nextRemind)
                    updatedEvent = modified
                    modified
                } else event
            }
        }

        repositoryScope.launch {
            updatedEvent?.let {
                database?.eventDao()?.updateReminder(it.id, it.reminderEnabled)
            }
        }

        prefs?.edit()?.putBoolean("remind_$eventId", nextRemind)?.apply()
    }

    fun addEvent(event: Event) {
        _events.update { listOf(event) + it }
        repositoryScope.launch {
            database?.eventDao()?.insert(EventEntity.fromEvent(event))
            // Also push to Supabase in background
            try {
                supabaseService.createEvent(
                    EventDto(
                        id = event.id,
                        mosqueId = event.mosqueId,
                        mosqueName = event.mosqueName,
                        mosqueImageUrl = event.mosqueImageUrl,
                        title = event.title,
                        description = event.description,
                        posterUrl = event.posterUrl,
                        timestampEpochMillis = event.timestampEpochMillis,
                        endTimestampEpochMillis = event.endTimestampEpochMillis,
                        dateMonth = event.dateMonth,
                        dateDay = event.dateDay,
                        dayOfWeek = event.dayOfWeek,
                        dateLabel = event.dateLabel,
                        fullDate = event.fullDate,
                        timeLabel = event.timeLabel,
                        location = event.location,
                        address = event.address,
                        category = event.category,
                        isTonight = event.isTonight,
                        isThisWeek = event.isThisWeek,
                        attendeeCount = event.attendeeCount,
                        capacity = event.capacity,
                        registrationRequired = event.registrationRequired,
                        cost = event.cost,
                        isOnline = event.isOnline,
                        language = event.language,
                        speakerIds = event.speakerIds,
                        speakerNames = event.speakerNames,
                        tags = event.tags
                    )
                )
            } catch (e: Throwable) {
                // Ignore sync failures to allow offline creation
            }
        }
    }
}
