package com.mosque.community.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mosque.community.ui.theme.MidnightCardSurface
import com.mosque.community.ui.theme.MidnightElevated
import com.mosque.community.ui.theme.MidnightInk
import com.mosque.community.ui.theme.MidnightOutline
import com.mosque.community.ui.theme.MidnightSurface
import com.mosque.community.ui.theme.SageAccent
import com.mosque.community.ui.theme.SoftGold
import com.mosque.community.ui.theme.SoftGoldContainer
import com.mosque.community.ui.theme.TextPrimary
import com.mosque.community.ui.theme.TextSecondary
import com.mosque.community.viewmodel.MosqueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPostEventScreen(
    viewModel: MosqueViewModel,
    onEventCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Tonight, Nov 19") }
    var time by remember { mutableStateOf("Fri 1:15 PM") }
    var description by remember { mutableStateOf("") }
    var notifyFollowers by remember { mutableStateOf(true) }
    var hasPoster by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Post an Event",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Drag Sheet Handle (Screenshot 3)
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MidnightOutline)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (!currentUser.isMosqueAdmin) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftGoldContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Admin Mode Required",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SoftGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Only authorized mosque administrators can post events to the public feed. Switch to an admin account in your profile.",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Dashed "Add Poster" Area (Screenshot 3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MidnightSurface)
                    .border(1.5.dp, if (hasPoster) SoftGold else MidnightOutline, RoundedCornerShape(16.dp))
                    .clickable { hasPoster = !hasPoster },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Poster",
                        tint = SoftGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (hasPoster) "Poster Selected ✓" else "Add poster",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (hasPoster) SoftGold else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Form Inputs Group with leading icons (Screenshot 3)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Event title", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                placeholder = { Text("Date", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                placeholder = { Text("Time", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Description", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = formFieldColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notify followers row with gold switch (Screenshot 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notify followers",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Switch(
                    checked = notifyFollowers,
                    onCheckedChange = { notifyFollowers = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MidnightInk,
                        checkedTrackColor = SoftGold,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = MidnightElevated,
                        uncheckedBorderColor = MidnightOutline
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Publish Event Pill Button (Screenshot 3)
            Button(
                onClick = {
                    val finalTitle = title.ifBlank { "Jumu'ah Khutbah: Patience in Hardship" }
                    viewModel.createEvent(
                        title = finalTitle,
                        date = date,
                        time = time,
                        description = description.ifBlank { "All community members are cordially invited." },
                        category = "Tonight",
                        location = "Main Prayer Hall",
                        posterUrl = "https://images.unsplash.com/photo-1542816417-0983c9c9ad53?auto=format&fit=crop&w=1200&q=80",
                        notifyFollowers = notifyFollowers
                    )
                    showSuccessDialog = true
                },
                enabled = currentUser.isMosqueAdmin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftGold,
                    contentColor = MidnightInk,
                    disabledContainerColor = MidnightElevated,
                    disabledContentColor = TextSecondary
                )
            ) {
                Text(
                    text = if (currentUser.isMosqueAdmin) "Publish event" else "Admin Login Required",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SageAccent,
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = { Text("Event Published", fontWeight = FontWeight.Bold, color = TextPrimary) },
                text = { Text("Your event has been posted to the Muslim Link feed.", color = TextSecondary) },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onEventCreated()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftGold, contentColor = MidnightInk)
                    ) {
                        Text("View Feed")
                    }
                },
                containerColor = MidnightCardSurface,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SoftGold,
    unfocusedBorderColor = MidnightOutline,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = SoftGold,
    focusedContainerColor = MidnightSurface,
    unfocusedContainerColor = MidnightSurface
)
