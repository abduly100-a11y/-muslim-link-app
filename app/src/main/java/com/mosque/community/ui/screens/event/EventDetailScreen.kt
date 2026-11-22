package com.mosque.community.ui.screens.event

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mosque.community.data.models.RsvpState
import com.mosque.community.util.CalendarUtils
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
fun EventDetailScreen(
    eventId: String,
    viewModel: MosqueViewModel,
    onBackClick: () -> Unit,
    onMosqueClick: (String) -> Unit,
    onSpeakerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.filteredEvents.collectAsState()
    val event = events.firstOrNull { it.id == eventId } ?: viewModel.getEventById(eventId)
    val context = LocalContext.current

    if (event == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightInk),
            contentAlignment = Alignment.Center
        ) {
            Text("Event not found", color = TextPrimary)
        }
        return
    }

    val speakers by viewModel.speakers.collectAsState()
    val eventSpeakers = speakers.filter { event.speakerIds.contains(it.id) }

    fun shareEvent() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, event.title)
            putExtra(Intent.EXTRA_TEXT, "${event.shareText}\n\n${event.description}")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Event via"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Event Details",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { shareEvent() }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = SoftGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MidnightInk,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            // Persistent Quick RSVP and Share Bottom Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MidnightSurface)
                    .border(1.dp, MidnightOutline)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { shareEvent() },
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = SoftGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val nextState = if (event.rsvpState == RsvpState.GOING) RsvpState.NOT_ATTENDING else RsvpState.GOING
                        viewModel.setEventRsvp(event.id, nextState)
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (event.rsvpState == RsvpState.GOING) SageAccent else SoftGold,
                        contentColor = MidnightInk
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    if (event.rsvpState == RsvpState.GOING) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("You're Going!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    } else {
                        Text("RSVP • Going", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
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
            // Hero Poster
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = event.posterUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MidnightInk.copy(alpha = 0.95f)),
                                    startY = 100f
                                )
                            )
                    )

                    // Badges on Poster
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftGold)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = event.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MidnightInk
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (event.isFree) SageContainer else SoftGoldContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (event.isFree) "FREE ADMISSION" else "$${event.cost}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (event.isFree) SageAccent else SoftGold
                            )
                        }

                        if (event.isOnline) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MidnightSurface)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ONLINE WEBINAR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Title & Date Header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = event.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date & Time Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftGoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = SoftGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = event.fullDate,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = event.timeLabel,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location / Venue Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMosqueClick(event.mosqueId) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SageAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.mosqueName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${event.location} • ${if (event.address.isNotBlank()) event.address else "Tap to view mosque"}",
                                fontSize = 12.sp,
                                color = SageAccent
                            )
                        }
                    }
                }
            }

            // RSVP & Attendance Status Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = SoftGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${event.attendeeCount} Community Members Attending",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }
                            if (event.capacity != null) {
                                Text(
                                    text = "Cap: ${event.capacity}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // RSVP State Buttons (Going / Interested / Not Attending)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RsvpChoiceChip(
                                label = "Going",
                                isSelected = event.rsvpState == RsvpState.GOING,
                                onClick = { viewModel.setEventRsvp(event.id, RsvpState.GOING) },
                                modifier = Modifier.weight(1f)
                            )
                            RsvpChoiceChip(
                                label = "Interested",
                                isSelected = event.rsvpState == RsvpState.INTERESTED,
                                onClick = { viewModel.setEventRsvp(event.id, RsvpState.INTERESTED) },
                                modifier = Modifier.weight(1f)
                            )
                            RsvpChoiceChip(
                                label = "Can't Go",
                                isSelected = event.rsvpState == RsvpState.NOT_ATTENDING,
                                onClick = { viewModel.setEventRsvp(event.id, RsvpState.NOT_ATTENDING) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Reminder Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    text = "Event Push Reminder",
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }
                            Switch(
                                checked = event.reminderEnabled,
                                onCheckedChange = { viewModel.toggleReminder(event.id) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MidnightInk,
                                    checkedTrackColor = SoftGold,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = MidnightElevated,
                                    uncheckedBorderColor = MidnightOutline
                                )
                            )
                        }

                        // Add to Device Calendar Option (When Going)
                        if (event.rsvpState == RsvpState.GOING) {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = { CalendarUtils.addToDeviceCalendar(context, event) },
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftGold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = SoftGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add to Device Calendar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Featured Scholar / Speaker(s)
            if (eventSpeakers.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FEATURED SCHOLAR / SPEAKER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 20.dp, bottom = 6.dp)
                    )
                }

                items(eventSpeakers, key = { it.id }) { speaker ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onSpeakerClick(speaker.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MidnightCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline)
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
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, SoftGold, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = speaker.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = speaker.title,
                                    fontSize = 12.sp,
                                    color = SageAccent
                                )
                            }
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = SoftGold, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Description
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "About This Event",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )

                        // Tags
                        if (event.tags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                event.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MidnightSurface)
                                            .border(1.dp, MidnightOutline, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "#$tag",
                                            fontSize = 11.sp,
                                            color = SoftGold,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Copy Link button
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Event Link", event.shareUrl)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Event link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link (${event.shareUrl})", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RsvpChoiceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SoftGold else MidnightElevated)
            .border(1.dp, if (isSelected) SoftGold else MidnightOutline, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MidnightInk else TextPrimary
        )
    }
}
