# Muslim Link — Islamic Events Discovery & Social Sharing Platform

A modern, native Android application built with **Kotlin** and **Jetpack Compose (Material 3)**. Muslim Link is designed exclusively to help Muslims discover, follow, attend, and share Islamic events (lectures, halaqas, conferences, workshops, Ramadan programs, and community gatherings) from local masajid, Islamic organizations, and renowned scholars.

---

## 🎨 Design & Palette
Designed specifically for elegance, warmth, and dark-environment adaptability (ideal for dimly lit prayer halls):
* **Midnight Ink (`#14171F`)**: Primary dark surface and background providing high contrast and OLED comfort.
* **Soft Gold (`#E2B96A`)**: Primary brand accent used for highlights, primary action buttons, prayer countdowns, and active states.
* **Sage Green (`#8CA38D`)**: Secondary accent for statuses, tags, reminder indicators, and prayer timetable subtitles.
* **Material 3 (M3)**: Full compliance with Material Design 3 guidelines with rounded corner elevation and accessible touch targets ($48 \times 48\text{ dp}$ minimum).

---

## 📱 Primary Navigation & Features

### 1. Discover Feed (`HomeScreen.kt`)
* **Glanceable Prayer Header**: Live countdown and Iqamah timetable dynamically calculated via astronomical solar equations for the user's primary mosque.
* **Filter Chips**: Instant filtering by **Tonight**, **This week**, **This weekend**, **Lectures**, and **Community iftar**.
* **Rich Event Cards**:
  * High-resolution warm community & mosque photography posters.
  * Title, date badge, time, and specific location inside the facility.
  * Quick RSVP toggle, reminder alarm scheduling, and host-mosque follow action.
  * Direct click to open comprehensive **Event Details**.

### 2. Mosques Directory & Qibla Compass (`MosqueFinderScreen.kt` & `MosqueProfileScreen.kt`)
* Searchable list of nearby masajid with distance sorting.
* **Live Hardware Qibla Compass**: Real-time sensor fusion using Android `SensorManager` (`TYPE_ROTATION_VECTOR`, accelerometer, and magnetometer) with Great Circle Kaaba bearing calculations.
* **Mosque Profiles**: Hero photography, follower counter, full-width Follow button, and an official **3-Column Timetable** (Adhan, congregational Iqamah, and Friday Jumu'ah prayer shifts).

### 3. Scholars & Speakers Directory (`SpeakerDirectoryScreen.kt` & `SpeakerProfileScreen.kt`)
* Dedicated directory of Islamic scholars, teachers, and lecturers.
* Topic filters (Tafseer, Youth, Family, Ethics, Spiritual Growth).
* **Speaker Profiles**: Full biography, languages spoken, follower counts, follow toggle, and list of upcoming hosted lectures and classes.

### 4. My Schedule (`MyScheduleScreen.kt`)
* Organized list of events the user is attending or interested in.
* Push notification reminder switches per event backed by Android `AlarmManager`.
* Guest login prompt when viewing personal schedule without an account.

### 5. Profile & Settings (`UserProfileScreen.kt`)
* Account management and role switching (Guest, Congregant, Mosque Admin).
* Astronomical prayer calculation method selection (ISNA, Umm al-Qura, MWL, Karachi).
* Access to Qibla compass and admin event publishing portal.

### 6. Event Detail & Sharing (`EventDetailScreen.kt`)
* Full event banner, tags (`#Spirituality`, `#Youth`), capacity limits, and pricing badge (Free / Paid).
* Multi-state RSVP system: **Going**, **Interested**, **Can't Go**.
* **Event Sharing**: Native Android Share sheet (`Intent.ACTION_SEND`) generating rich invitations with deep links across WhatsApp, Telegram, SMS, Email, and social platforms.
* Copyable public link: `https://muslimlink.app/event/{event-id}`.
* **System Calendar Export**: Direct insert via `CalendarContract` using standardized start and end epoch timestamps.

### 7. Admin Event Portal (`AdminPostEventScreen.kt`)
* Role-guarded publishing interface for verified mosque administrators.
* Event title, category, date, time, venue, and "Notify Followers" push broadcast toggle.

---

## 🔗 Deep Linking Configuration
Muslim Link intercepts standard HTTPS URLs and custom URI schemes:
* `https://muslimlink.app/event/{eventId}` or `muslimlink://event/{eventId}` → Opens **EventDetailScreen**
* `https://muslimlink.app/speaker/{speakerId}` or `muslimlink://speaker/{speakerId}` → Opens **SpeakerProfileScreen**
* `https://muslimlink.app/mosque/{mosqueId}` or `muslimlink://mosque/{mosqueId}` → Opens **MosqueProfileScreen**

---

## 🏗️ Production Architecture

```
app/src/main/
├── AndroidManifest.xml                  # Permissions (Location, Notifications, Alarms) & Receivers
├── java/com/mosque/community/
│   ├── MainActivity.kt                  # Compose NavHost, 5-tab BottomBar & Notification initialization
│   ├── data/
│   │   ├── local/                       # Room Persistence Layer
│   │   │   ├── entity/
│   │   │   │   ├── MosqueEntity.kt      # Room Entity for Masajid & Prayer Timetables
│   │   │   │   ├── EventEntity.kt       # Room Entity for Community Events
│   │   │   │   └── SpeakerEntity.kt     # Room Entity for Scholars & Speakers
│   │   │   ├── dao/
│   │   │   │   ├── MosqueDao.kt         # Reactive Flow Mosque queries
│   │   │   │   ├── EventDao.kt          # Reactive Flow Event queries & RSVP updates
│   │   │   │   └── SpeakerDao.kt        # Reactive Flow Speaker queries
│   │   │   └── MosqueDatabase.kt        # Android Room Database singleton
│   │   ├── models/
│   │   │   ├── Event.kt                 # Event entity with epoch timestamps, badges & RSVP
│   │   │   ├── Mosque.kt                # Mosque profile entity
│   │   │   ├── Speaker.kt               # Scholar/Speaker entity
│   │   │   ├── RsvpState.kt             # RSVP states: GOING, INTERESTED, NOT_ATTENDING
│   │   │   ├── PrayerTimes.kt           # Adhan & Iqamah schedules
│   │   │   ├── QiblaInfo.kt             # Qibla compass bearing calculations
│   │   │   └── User.kt                  # User model & roles (Guest, Congregant, Admin)
│   │   └── repository/
│   │       └── MosqueRepository.kt      # Offline-first repository with persistent caching
│   ├── viewmodel/
│   │   └── MosqueViewModel.kt           # Business logic, reactive filtering, RSVP, & prayer status
│   ├── util/
│   │   ├── CalendarUtils.kt             # Device calendar export with epoch timestamps
│   │   ├── prayer/
│   │   │   └── AstronomicalPrayerCalculator.kt # Real-time astronomical prayer engine & Qibla math
│   │   └── notification/
│   │       ├── NotificationHelper.kt    # Notification channels & AlarmManager scheduling
│   │       ├── AlarmReceiver.kt         # BroadcastReceiver for exact notification delivery
│   │       └── BootReceiver.kt          # Device reboot reminder restoration
│   ├── ui/
│   │   ├── components/
│   │   │   ├── EventCard.kt             # Luma-style card with Follow, RSVP & Share actions
│   │   │   ├── AuthDialog.kt            # Role selection / login dialog
│   │   │   ├── PrayerCountdownHeader.kt # Dynamic prayer countdown header
│   │   │   └── PrayerTimesTable.kt      # Adhan vs Iqamah prayer schedule card
│   │   ├── navigation/
│   │   │   ├── Screen.kt                # Navigation routes and icons
│   │   │   └── BottomBar.kt             # Material 3 navigation bar
│   │   ├── screens/
│   │   │   ├── home/HomeScreen.kt
│   │   │   ├── event/EventDetailScreen.kt
│   │   │   ├── speakers/SpeakerDirectoryScreen.kt
│   │   │   ├── speakers/SpeakerProfileScreen.kt
│   │   │   ├── finder/MosqueFinderScreen.kt
│   │   │   ├── profile/MosqueProfileScreen.kt
│   │   │   ├── schedule/MyScheduleScreen.kt
│   │   │   ├── profile/UserProfileScreen.kt
│   │   │   └── admin/AdminPostEventScreen.kt
│   │   └── theme/
│   │       ├── Color.kt                 # Midnight Ink, Soft Gold, Sage tokens
│   │       ├── Theme.kt                 # Material 3 Dark theme setup
│   │       └── Type.kt                  # Material 3 Typography scales
│   └── res/
│       ├── values/                      # colors.xml, strings.xml, themes.xml
│       └── mipmap-anydpi-v26/           # Adaptive vector launcher icons
```

---

## 🧪 Testing & Validation
Unit test suites cover:
* `MosqueRepositoryTest.kt`: Validates initial masajid/speakers loading, follow/unfollow toggle and counter updates, category following, RSVP state transitions (`GOING`, `INTERESTED`, `NOT_ATTENDING`), attendee counts, and bookmark compatibility.
* `MosqueViewModelTest.kt`: Validates filtering, speaker and mosque lookups, event creation, calculation methods, and Qibla compass bearing alignment calculations.
* `AstronomicalPrayerCalculatorTest.kt`: Validates solar coordinates equation calculations, Qibla bearing from coordinates to Makkah (~58.4° from Springfield, IL), distance to Kaaba (~6,840 mi), Haversine distance, and all calculation methods (ISNA, Umm al-Qura, MWL, Karachi).

---

## 🚀 How to Run in Android Studio
1. Launch **Android Studio** (Hedgehog, Iguana, Jellyfish, or newer).
2. Click **Open** and select this directory:
   `C:\Users\ayacoob\Desktop\New folder (3)\mosque`
3. Allow Gradle to sync dependencies (Android Studio uses its bundled JDK 17+ automatically).
4. Run the project on an Android Emulator or connected physical device running Android 8.0 (API 26) or higher.
