package com.mosque.community.ui.screens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mosque.community.data.models.EventFilter
import com.mosque.community.ui.components.EventCard
import com.mosque.community.ui.components.PrayerCountdownHeader
import com.mosque.community.ui.theme.MidnightInk
import com.mosque.community.ui.theme.MidnightOutline
import com.mosque.community.ui.theme.MidnightSurface
import com.mosque.community.ui.theme.SoftGold
import com.mosque.community.ui.theme.TextPrimary
import com.mosque.community.ui.theme.TextSecondary
import com.mosque.community.viewmodel.MosqueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MosqueViewModel,
    onMosqueClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onEventClick: (String) -> Unit = {}
) {
    val events by viewModel.filteredEvents.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val primaryMosque by viewModel.primaryMosque.collectAsState()
    val prayerResult by viewModel.livePrayerCalculation.collectAsState()

    var showAuthDialog by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val displayedEvents = events.filter { event ->
        if (searchQuery.isBlank()) true
        else {
            event.title.contains(searchQuery, ignoreCase = true) ||
            event.mosqueName.contains(searchQuery, ignoreCase = true) ||
            event.speakerNames.any { it.contains(searchQuery, ignoreCase = true) } ||
            event.category.contains(searchQuery, ignoreCase = true) ||
            event.tags.any { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    if (showAuthDialog) {
        com.mosque.community.ui.components.AuthDialog(
            viewModel = viewModel,
            onDismiss = { showAuthDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search lectures, scholars, masajid...", color = TextSecondary, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MidnightSurface,
                                unfocusedContainerColor = MidnightSurface,
                                focusedBorderColor = SoftGold,
                                unfocusedBorderColor = MidnightOutline,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = SoftGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Muslim Link",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) searchQuery = ""
                    }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Clear else Icons.Default.Search,
                            contentDescription = if (isSearchActive) "Close Search" else "Search",
                            tint = if (isSearchActive) SoftGold else TextPrimary
                        )
                    }
                    IconButton(onClick = { showAuthDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Account",
                            tint = if (currentUser.isGuest) TextSecondary else SoftGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightInk,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = MidnightInk,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Live Iqamah Countdown Header
            if (primaryMosque != null && !isSearchActive) {
                item {
                    val defaultMosque = primaryMosque!!
                    PrayerCountdownHeader(
                        mosque = defaultMosque,
                        nextPrayerName = prayerResult.nextPrayerName,
                        iqamahTime = prayerResult.nextIqamahTime,
                        minutesRemaining = prayerResult.minutesRemaining,
                        onViewTimetable = { onMosqueClick(defaultMosque.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Filter Chips Carousel
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filtersWithIcons = listOf(
                        Pair(EventFilter.ALL, Icons.Default.Explore),
                        Pair(EventFilter.TONIGHT, Icons.Default.CalendarToday),
                        Pair(EventFilter.THIS_WEEK, Icons.Default.CalendarToday),
                        Pair(EventFilter.WEEKEND, Icons.Default.Weekend),
                        Pair(EventFilter.LECTURES, Icons.Default.MenuBook),
                        Pair(EventFilter.COMMUNITY_IFTAR, Icons.Default.People)
                    )

                    filtersWithIcons.forEach { (filter, icon) ->
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectFilter(filter) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) MidnightInk else TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = filter.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MidnightInk else TextPrimary
                                    )
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MidnightSurface,
                                selectedContainerColor = SoftGold,
                                labelColor = TextPrimary,
                                selectedLabelColor = MidnightInk
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MidnightOutline,
                                selectedBorderColor = SoftGold,
                                borderWidth = 1.dp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Events Feed List
            if (displayedEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No events matching '$searchQuery'" else "No events found",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            } else {
                items(
                    items = displayedEvents,
                    key = { it.id }
                ) { event ->
                    EventCard(
                        event = event,
                        isGuest = currentUser.isGuest,
                        onFollowClick = { viewModel.toggleFollowMosque(it) },
                        onRsvpClick = { viewModel.toggleBookmark(it) },
                        onMosqueClick = onMosqueClick,
                        onRequireAuth = { showAuthDialog = true },
                        onEventClick = onEventClick
                    )
                }
            }
        }
    }
}
