package com.mosque.community

import com.mosque.community.data.models.Event
import com.mosque.community.data.models.RsvpState
import com.mosque.community.data.repository.MosqueRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MosqueRepositoryTest {

    private lateinit var repository: MosqueRepository

    @Before
    fun setUp() {
        repository = MosqueRepository()
    }

    @Test
    fun testInitialMosquesLoaded() {
        val mosques = repository.mosques.value
        assertTrue("Mosques list should not be empty", mosques.isNotEmpty())
        val firstMosque = mosques.first()
        assertEquals("Al-Noor Islamic Cultural Center", firstMosque.name)
        assertTrue("First mosque should be followed by default", firstMosque.isFollowed)
    }

    @Test
    fun testInitialSpeakersLoaded() {
        val speakers = repository.speakers.value
        assertTrue("Speakers list should not be empty", speakers.isNotEmpty())
        val firstSpeaker = speakers.first()
        assertEquals("Dr. Tariq Rahman", firstSpeaker.name)
        assertTrue("Dr. Tariq should be followed initially", firstSpeaker.isFollowed)
    }

    @Test
    fun testToggleFollowSpeakerUpdatesCount() {
        val speakers = repository.speakers.value
        val target = speakers.first { !it.isFollowed }
        val originalCount = target.followerCount

        repository.toggleFollowSpeaker(target.id)
        val updated = repository.speakers.value.first { it.id == target.id }
        assertTrue("Speaker should now be followed", updated.isFollowed)
        assertEquals(originalCount + 1, updated.followerCount)

        repository.toggleFollowSpeaker(target.id)
        val reverted = repository.speakers.value.first { it.id == target.id }
        assertFalse("Speaker should now be unfollowed", reverted.isFollowed)
        assertEquals(originalCount, reverted.followerCount)
    }

    @Test
    fun testToggleFollowCategory() {
        val initialCategories = repository.followedCategories.value
        assertTrue(initialCategories.contains("Lectures"))

        repository.toggleFollowCategory("Lectures")
        assertFalse(repository.followedCategories.value.contains("Lectures"))

        repository.toggleFollowCategory("Lectures")
        assertTrue(repository.followedCategories.value.contains("Lectures"))
    }

    @Test
    fun testSetEventRsvpUpdatesStateAndAttendees() {
        val event = repository.events.value.first { it.id == "e2" }
        assertEquals(RsvpState.NOT_ATTENDING, event.rsvpState)
        val initialAttendees = event.attendeeCount

        // RSVP Going
        repository.setEventRsvp("e2", RsvpState.GOING)
        val goingEvent = repository.events.value.first { it.id == "e2" }
        assertEquals(RsvpState.GOING, goingEvent.rsvpState)
        assertTrue(goingEvent.isBookmarked)
        assertEquals(initialAttendees + 1, goingEvent.attendeeCount)

        // RSVP Interested
        repository.setEventRsvp("e2", RsvpState.INTERESTED)
        val interestedEvent = repository.events.value.first { it.id == "e2" }
        assertEquals(RsvpState.INTERESTED, interestedEvent.rsvpState)
        assertTrue(interestedEvent.isBookmarked)
        assertEquals(initialAttendees, interestedEvent.attendeeCount) // count decreased from going

        // RSVP Not Attending
        repository.setEventRsvp("e2", RsvpState.NOT_ATTENDING)
        val notAttendingEvent = repository.events.value.first { it.id == "e2" }
        assertEquals(RsvpState.NOT_ATTENDING, notAttendingEvent.rsvpState)
        assertFalse(notAttendingEvent.isBookmarked)
    }

    @Test
    fun testToggleFollowMosqueUpdatesCountAndSynchronizesEvents() {
        val mosques = repository.mosques.value
        val targetMosque = mosques.first { !it.isFollowed }
        val originalFollowerCount = targetMosque.followerCount

        // Follow the mosque
        repository.toggleFollowMosque(targetMosque.id)

        val updatedMosques = repository.mosques.value
        val updatedTarget = updatedMosques.first { it.id == targetMosque.id }

        assertTrue("Mosque should now be followed", updatedTarget.isFollowed)
        assertEquals(originalFollowerCount + 1, updatedTarget.followerCount)

        // Events belonging to this mosque should also reflect isMosqueFollowed = true
        val targetEvents = repository.events.value.filter { it.mosqueId == targetMosque.id }
        targetEvents.forEach { event ->
            assertTrue("Event should reflect followed status of mosque", event.isMosqueFollowed)
        }

        // Unfollow the mosque
        repository.toggleFollowMosque(targetMosque.id)
        val unfollowedTarget = repository.mosques.value.first { it.id == targetMosque.id }
        assertFalse("Mosque should now be unfollowed", unfollowedTarget.isFollowed)
        assertEquals(originalFollowerCount, unfollowedTarget.followerCount)
    }

    @Test
    fun testToggleBookmarkAndReminder() {
        val initialEvent = repository.events.value.first()
        val originalBookmark = initialEvent.isBookmarked
        val originalReminder = initialEvent.reminderEnabled

        repository.toggleBookmark(initialEvent.id)
        val updatedEvent1 = repository.events.value.first { it.id == initialEvent.id }
        assertEquals(!originalBookmark, updatedEvent1.isBookmarked)

        repository.toggleReminder(initialEvent.id)
        val updatedEvent2 = repository.events.value.first { it.id == initialEvent.id }
        assertEquals(!originalReminder, updatedEvent2.reminderEnabled)
    }

    @Test
    fun testAddEventPrependsToRepository() {
        val initialCount = repository.events.value.size
        val newEvent = Event(
            id = "test-event-123",
            mosqueId = "m1",
            mosqueName = "Al-Noor Islamic Cultural Center",
            mosqueImageUrl = "https://example.com/mosque.jpg",
            title = "Test Community Halaqah",
            description = "Test description",
            posterUrl = "https://example.com/poster.jpg",
            dateLabel = "Tonight",
            fullDate = "Tonight, Nov 19",
            timeLabel = "8:00 PM",
            location = "Main Hall",
            category = "Lectures",
            isTonight = true
        )

        repository.addEvent(newEvent)

        val events = repository.events.value
        assertEquals(initialCount + 1, events.size)
        assertEquals("test-event-123", events.first().id)
        assertEquals("Test Community Halaqah", events.first().title)
    }

    @Test
    fun testAuthenticationTransitions() {
        // Starts as Guest
        assertTrue(repository.currentUser.value.isGuest)

        // Login as Congregant
        repository.loginAs(com.mosque.community.data.models.User.SampleCongregant)
        assertTrue(repository.currentUser.value.isCongregant)
        assertFalse(repository.currentUser.value.isMosqueAdmin)

        // Login as Admin
        repository.loginAs(com.mosque.community.data.models.User.SampleAdmin)
        assertTrue(repository.currentUser.value.isMosqueAdmin)
        assertEquals("m1", repository.currentUser.value.adminMosqueId)

        // Logout returns to Guest
        repository.logout()
        assertTrue(repository.currentUser.value.isGuest)
    }

    @Test
    fun testUpdatePrayerTimes() {
        val targetMosque = repository.mosques.value.first()
        val originalAsr = targetMosque.prayerTimes.asrIqamah

        val updatedTimes = targetMosque.prayerTimes.copy(asrIqamah = "5:15 PM")
        repository.updatePrayerTimes(targetMosque.id, updatedTimes)

        val updatedMosque = repository.mosques.value.first { it.id == targetMosque.id }
        assertEquals("5:15 PM", updatedMosque.prayerTimes.asrIqamah)
    }
}
