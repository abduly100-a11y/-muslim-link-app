package com.mosque.community.ui.screens.schedule

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mosque.community.util.CalendarUtils
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mosque.community.data.models.Event
import com.mosque.community.ui.theme.MidnightCardSurface
import com.mosque.community.ui.theme.MidnightElevated
import com.mosque.community.ui.theme.MidnightInk
import com.mosque.community.ui.theme.MidnightOutline
import com.mosque.community.ui.theme.MidnightSurface
import com.mosque.community.ui.theme.SageAccent
import com.mosque.community.ui.theme.SoftGold
import com.mosque.community.ui.theme.TextPrimary
import com.mosque.community.ui.theme.TextSecondary
import com.mosque.community.viewmodel.MosqueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScheduleScreen(
    viewModel: MosqueViewModel,
    onNavigateToDiscover: () -> Unit,
    onRequireLogin: () -> Unit,
    modifier: Modifier = Modifier,
    onEventClick: (String) -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allEvents by viewModel.filteredEvents.collectAsState()
    
    // In our domain model, bookmarked events represent RSVPs/Organized lectures
    val myEvents = allEvents.filter { it.isBookmarked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Schedule",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Your organized lectures & notifications",
                            fontSize = 12.sp,
                            color = SageAccent
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
        if (currentUser.isGuest) {
            // Guest Lock State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MidnightSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = SoftGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Personal Schedule Locked",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to organize your personal calendar, RSVP to community lectures, and enable event reminders.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRequireLogin,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftGold,
                        contentColor = MidnightInk
                    )
                ) {
                    Text("Sign In / Select Profile", fontWeight = FontWeight.Bold)
                }
            }
        } else if (myEvents.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Events on Your Schedule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explore upcoming lectures and iftars on the Discover feed and click RSVP to add them here.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToDiscover,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidnightElevated,
                        contentColor = SoftGold
                    )
                ) {
                    Text("Browse Events")
                }
            }
        } else {
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "ATTENDING / SAVED (${myEvents.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        letterSpacing = 1.sp
                    )
                }

                items(myEvents, key = { it.id }) { event ->
                    ScheduleEventCard(
                        event = event,
                        onToggleReminder = { viewModel.toggleReminder(event.id) },
                        onRemove = { viewModel.toggleBookmark(event.id) },
                        onClick = { onEventClick(event.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleEventCard(
    event: Event,
    onToggleReminder: () -> Unit,
    onRemove: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.mosqueName,
                    fontSize = 12.sp,
                    color = SageAccent,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { CalendarUtils.addToDeviceCalendar(context, event) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Export to Calendar",
                            tint = SoftGold,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Remove",
                            tint = SageAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${event.dateLabel} • ${event.timeLabel}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification / Reminder Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MidnightSurface)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (event.reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = if (event.reminderEnabled) SoftGold else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (event.reminderEnabled) "Reminder Active" else "Notify Me",
                        fontSize = 12.sp,
                        color = if (event.reminderEnabled) TextPrimary else TextSecondary,
                        fontWeight = if (event.reminderEnabled) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                Switch(
                    checked = event.reminderEnabled,
                    onCheckedChange = { onToggleReminder() },
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
