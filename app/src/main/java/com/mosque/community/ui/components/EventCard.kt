package com.mosque.community.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mosque.community.data.models.Event
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

@Composable
fun EventCard(
    event: Event,
    isGuest: Boolean,
    onFollowClick: (String) -> Unit,
    onRsvpClick: (String) -> Unit,
    onMosqueClick: (String) -> Unit,
    onRequireAuth: () -> Unit,
    modifier: Modifier = Modifier,
    onEventClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (onEventClick != null) Modifier.clickable { onEventClick(event.id) } else Modifier),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 1. Cover Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = event.posterUrl,
                    contentDescription = event.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )

                // Category pill on top-left
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightInk.copy(alpha = 0.85f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.category,
                        color = SoftGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Luma-Style Date Rail & Event Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Editorial Date Rail Badge (Luma style)
                Column(
                    modifier = Modifier
                        .width(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MidnightSurface)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = event.dateMonth,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SoftGold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = event.dateDay,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = event.dayOfWeek,
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Event Content & Meta
                Column(modifier = Modifier.weight(1f)) {
                    // Host Mosque attribution
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onMosqueClick(event.mosqueId) }
                    ) {
                        Text(
                            text = event.mosqueName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SageAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Title
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 21.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Time & Location
                    Text(
                        text = "${event.timeLabel} • ${event.location}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Social proof / attendance
                    Text(
                        text = "${if (event.isBookmarked) event.attendeeCount + 1 else event.attendeeCount} attending",
                        fontSize = 11.sp,
                        color = SoftGold,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MidnightOutline.copy(alpha = 0.6f))
            )

            // 3. Actions Row: Social Share + Gated RSVP + Gated Follow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Social Share Action (Open to all)
                OutlinedButton(
                    onClick = {
                        shareEventInvitation(context, event)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = SoftGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // RSVP Button (STRICTLY GATED TO LOGGED IN USERS)
                    if (isGuest) {
                        OutlinedButton(
                            onClick = onRequireAuth,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text("Sign in to RSVP", fontSize = 11.sp)
                        }
                    } else {
                        if (event.isBookmarked) {
                            FilledTonalButton(
                                onClick = { onRsvpClick(event.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = SageContainer,
                                    contentColor = SageAccent
                                ),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Attending", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            FilledTonalButton(
                                onClick = { onRsvpClick(event.id) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = SoftGold,
                                    contentColor = MidnightInk
                                ),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                            ) {
                                Text("RSVP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Follow Host Mosque Button (GATED)
                    if (!isGuest) {
                        if (event.isMosqueFollowed) {
                            OutlinedButton(
                                onClick = { onFollowClick(event.mosqueId) },
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SageAccent),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SageAccent),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Text("Following", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onFollowClick(event.mosqueId) },
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoftGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftGold),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Text("+ Follow", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun shareEventInvitation(context: Context, event: Event) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, event.shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Invite friends to ${event.title}")
    context.startActivity(shareIntent)
}
