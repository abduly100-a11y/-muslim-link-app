package com.mosque.community

import com.mosque.community.data.models.CalculationMethod
import com.mosque.community.data.models.DistanceRadius
import com.mosque.community.data.models.EventFilter
import com.mosque.community.data.models.RsvpState
import com.mosque.community.data.repository.MosqueRepository
import com.mosque.community.viewmodel.MosqueViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MosqueViewModelTest {

    private lateinit var repository: MosqueRepository
    private lateinit var viewModel: MosqueViewModel

    @Before
    fun setUp() {
        repository = MosqueRepository()
        viewModel = MosqueViewModel(repository)
    }

    @Test
    fun testSelectFilterUpdatesState() {
        assertEquals(EventFilter.ALL, viewModel.selectedFilter.value)

        viewModel.selectFilter(EventFilter.TONIGHT)
        assertEquals(EventFilter.TONIGHT, viewModel.selectedFilter.value)

        viewModel.selectFilter(EventFilter.LECTURES)
        assertEquals(EventFilter.LECTURES, viewModel.selectedFilter.value)

        viewModel.selectFilter(EventFilter.COMMUNITY_IFTAR)
        assertEquals(EventFilter.COMMUNITY_IFTAR, viewModel.selectedFilter.value)

        viewModel.selectFilter(EventFilter.WEEKEND)
        assertEquals(EventFilter.WEEKEND, viewModel.selectedFilter.value)
    }

    @Test
    fun testGetMosqueById() {
        val mosque = viewModel.getMosqueById("m1")
        assertNotNull(mosque)
        assertEquals("Al-Noor Islamic Cultural Center", mosque?.name)
    }

    @Test
    fun testGetEventsByMosque() {
        val events = viewModel.getEventsByMosque("m1")
        assertTrue("Al-Noor should have events", events.isNotEmpty())
        events.forEach { event ->
            assertEquals("m1", event.mosqueId)
        }
    }

    @Test
    fun testSpeakerLookupsAndEventAssociation() {
        val speaker = viewModel.getSpeakerById("s1")
        assertNotNull(speaker)
        assertEquals("Dr. Tariq Rahman", speaker?.name)

        val speakerEvents = viewModel.getEventsBySpeaker("s1")
        assertTrue("Dr. Tariq should have at least 1 event", speakerEvents.isNotEmpty())
        assertEquals("Finding Peace in Trials & Tribulations", speakerEvents.first().title)
    }

    @Test
    fun testGetEventById() {
        val event = viewModel.getEventById("e1")
        assertNotNull(event)
        assertEquals("Finding Peace in Trials & Tribulations", event?.title)
        assertEquals("https://muslimlink.app/event/e1", event?.shareUrl)
        assertTrue(event!!.shareText.contains("https://muslimlink.app/event/e1"))
        assertTrue(event.shareText.contains("Masjid Al-Noor"))
    }

    @Test
    fun testSetEventRsvpFromViewModel() {
        val event = viewModel.getEventById("e2")
        assertNotNull(event)
        assertEquals(RsvpState.NOT_ATTENDING, event!!.rsvpState)

        viewModel.setEventRsvp("e2", RsvpState.GOING)
        val updated = viewModel.getEventById("e2")
        assertEquals(RsvpState.GOING, updated?.rsvpState)
    }

    @Test
    fun testCreateEvent() {
        val success = viewModel.createEvent(
            title = "Special Taraweeh Announcement",
            date = "Tonight, Nov 19",
            time = "8:30 PM",
            description = "Congregational evening program and recitation.",
            category = "Lectures",
            location = "Main Prayer Hall",
            posterUrl = "",
            notifyFollowers = true,
            speakerNames = listOf("Dr. Tariq Rahman")
        )

        assertTrue(success)
        val created = repository.events.value.first()
        assertEquals("Special Taraweeh Announcement", created.title)
        assertTrue(created.isTonight)
        assertTrue(created.reminderEnabled)
    }

    @Test
    fun testRadiusFiltering() {
        // Default radius ALL should return all 4 mosques
        assertEquals(4, viewModel.filteredNearbyMosques.value.size)

        // Filter 1 mile should only return Al-Noor (0.8 mi)
        viewModel.selectRadius(DistanceRadius.ONE_MILE)
        val filtered = viewModel.filteredNearbyMosques.value
        assertEquals(1, filtered.size)
        assertEquals("Al-Noor Islamic Cultural Center", filtered.first().name)

        // Filter 5 miles should return Al-Noor, Medina, and Baitul Mukarram (3 mosques)
        viewModel.selectRadius(DistanceRadius.FIVE_MILES)
        assertEquals(3, viewModel.filteredNearbyMosques.value.size)
    }

    @Test
    fun testCalculationMethodAndQiblaAlignment() {
        assertEquals(CalculationMethod.ISNA, viewModel.selectedCalculationMethod.value)

        viewModel.selectCalculationMethod(CalculationMethod.UMM_AL_QURA)
        assertEquals(CalculationMethod.UMM_AL_QURA, viewModel.selectedCalculationMethod.value)

        // Aligning heading near 58.4 should set isAligned = true
        viewModel.updateCompassHeading(58.4f)
        assertTrue(viewModel.qiblaInfo.value.isAligned)

        // Misaligning heading to 180 should set isAligned = false
        viewModel.updateCompassHeading(180f)
        assertFalse(viewModel.qiblaInfo.value.isAligned)
    }

    @Test
    fun testSyncWithBackend() {
        // Calling syncWithBackend shouldn't crash and should complete
        viewModel.syncWithBackend()
        // Ensure state remains valid
        assertTrue(viewModel.mosques.value.isNotEmpty())
        assertTrue(viewModel.events.value.isNotEmpty())
        assertTrue(viewModel.speakers.value.isNotEmpty())
    }
}
