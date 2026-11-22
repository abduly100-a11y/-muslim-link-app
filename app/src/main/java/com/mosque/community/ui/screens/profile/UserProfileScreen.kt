package com.mosque.community.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mosque.community.data.models.User
import com.mosque.community.ui.theme.MidnightCardSurface
import com.mosque.community.ui.theme.MidnightElevated
import com.mosque.community.ui.theme.MidnightInk
import com.mosque.community.ui.theme.MidnightOutline
import com.mosque.community.ui.theme.MidnightSurface
import com.mosque.community.ui.theme.SageAccent
import com.mosque.community.ui.theme.SageContainer
import com.mosque.community.ui.theme.SoftGold
import com.mosque.community.ui.theme.SoftGoldContainer
import com.mosque.community.ui.theme.TextPrimary
import com.mosque.community.ui.theme.TextSecondary
import com.mosque.community.viewmodel.MosqueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: MosqueViewModel,
    onNavigateToPostEvent: () -> Unit,
    onNavigateToQibla: () -> Unit,
    onMosqueClick: (String) -> Unit,
    onSpeakerClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val mosques by viewModel.mosques.collectAsState()
    val speakers by viewModel.speakers.collectAsState()
    val followedCategories by viewModel.followedCategories.collectAsState()

    var notifyEvents by remember { mutableStateOf(true) }
    var notifyReminders by remember { mutableStateOf(true) }

    val allAvailableCategories = listOf(
        "Lectures", "Youth Program", "Sisters Program",
        "Community iftar", "Quran Class", "Ramadan Program",
        "Conference", "Workshop"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile & Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Identification Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (currentUser.isMosqueAdmin) SoftGoldContainer else SageContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (currentUser.isGuest) "?" else currentUser.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (currentUser.isMosqueAdmin) SoftGold else SageAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = currentUser.role.label,
                                    fontSize = 12.sp,
                                    color = if (currentUser.isMosqueAdmin) SoftGold else SageAccent
                                )
                            }
                            if (!currentUser.isGuest) {
                                IconButton(onClick = { viewModel.logout() }) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Sign Out",
                                        tint = TextSecondary
                                    )
                                }
                            }
                        }

                        if (currentUser.isGuest) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loginAs(User.SampleCongregant) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SoftGold,
                                    contentColor = MidnightInk
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign In as Registered Member (Demo)", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.loginAs(User.SampleAdmin) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign In as Mosque Admin (Demo)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Organizer Analytics & Admin Tools (When Organizer / Admin)
            if (currentUser.isMosqueAdmin) {
                item {
                    Text(
                        text = "ORGANIZER ANALYTICS & TOOLS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftGoldContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = SoftGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analytics: ${currentUser.adminMosqueName ?: "Masjid Al-Noor"}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4 Key Analytics KPI Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AnalyticsMetricBox(
                                    label = "Total Views",
                                    value = "3,420",
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricBox(
                                    label = "Event Shares",
                                    value = "486",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AnalyticsMetricBox(
                                    label = "Total RSVPs",
                                    value = "192",
                                    modifier = Modifier.weight(1f)
                                )
                                AnalyticsMetricBox(
                                    label = "Followers",
                                    value = "1,420",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onNavigateToPostEvent,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SoftGold,
                                    contentColor = MidnightInk
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publish New Event", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Event Interests / Category Following Section
            if (!currentUser.isGuest) {
                item {
                    Text(
                        text = "FOLLOWED TOPICS & EVENT INTERESTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Customize your discover feed & event notifications",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allAvailableCategories.forEach { category ->
                                    val isFollowed = followedCategories.contains(category)
                                    FilterChip(
                                        selected = isFollowed,
                                        onClick = { viewModel.toggleFollowCategory(category) },
                                        label = {
                                            Text(
                                                text = if (isFollowed) "✓ $category" else "+ $category",
                                                fontSize = 11.sp,
                                                fontWeight = if (isFollowed) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = MidnightCardSurface,
                                            labelColor = TextSecondary,
                                            selectedContainerColor = SoftGold,
                                            selectedLabelColor = MidnightInk
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = MidnightOutline,
                                            selectedBorderColor = SoftGold,
                                            borderWidth = 1.dp
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notification Preferences
            if (!currentUser.isGuest) {
                item {
                    Text(
                        text = "NOTIFICATION PREFERENCES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("New Event Alerts", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text("Notify when followed masajid or scholars post", fontSize = 11.sp, color = TextSecondary)
                                }
                                Switch(
                                    checked = notifyEvents,
                                    onCheckedChange = { notifyEvents = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MidnightInk,
                                        checkedTrackColor = SoftGold,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = MidnightElevated,
                                        uncheckedBorderColor = MidnightOutline
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("RSVP & Schedule Reminders", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    Text("Receive 1-hour countdown reminders before events", fontSize = 11.sp, color = TextSecondary)
                                }
                                Switch(
                                    checked = notifyReminders,
                                    onCheckedChange = { notifyReminders = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MidnightInk,
                                        checkedTrackColor = SoftGold,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = MidnightElevated,
                                        uncheckedBorderColor = MidnightOutline
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Followed Scholars & Speakers
            if (!currentUser.isGuest) {
                val followedSpeakers = speakers.filter { it.isFollowed }
                item {
                    Text(
                        text = "FOLLOWED SCHOLARS (${followedSpeakers.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                if (followedSpeakers.isEmpty()) {
                    item {
                        Text(
                            text = "You are not following any scholars yet.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                } else {
                    items(followedSpeakers, key = { it.id }) { speaker ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSpeakerClick(speaker.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = speaker.imageUrl,
                                    contentDescription = speaker.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, SoftGold, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = speaker.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = speaker.title,
                                        fontSize = 11.sp,
                                        color = SageAccent
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.toggleFollowSpeaker(speaker.id) },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SageAccent),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SageAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Following", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Followed Masajid / Communities
            if (!currentUser.isGuest) {
                val followedMosques = mosques.filter { it.isFollowed }
                item {
                    Text(
                        text = "FOLLOWED MASAJID (${followedMosques.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                items(followedMosques, key = { it.id }) { mosque ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMosqueClick(mosque.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = SageAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = mosque.name,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Utilities: Qibla Compass
            item {
                Text(
                    text = "UTILITIES & TOOLS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToQibla() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Qibla Direction",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Find the exact direction towards the Ka'bah",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsMetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MidnightSurface)
            .border(1.dp, MidnightOutline, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SoftGold)
        }
    }
}
