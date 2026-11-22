package com.mosque.community.ui.screens.finder

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mosque.community.data.models.CalculationMethod
import com.mosque.community.data.models.DistanceRadius
import com.mosque.community.data.models.Mosque
import com.mosque.community.ui.theme.MidnightCardSurface
import com.mosque.community.ui.theme.MidnightElevated
import com.mosque.community.ui.theme.MidnightInk
import com.mosque.community.ui.theme.MidnightOutline
import com.mosque.community.ui.theme.SageAccent
import com.mosque.community.ui.theme.SageContainer
import com.mosque.community.ui.theme.SoftGold
import com.mosque.community.ui.theme.SoftGoldContainer
import com.mosque.community.ui.theme.TextPrimary
import com.mosque.community.ui.theme.TextSecondary
import com.mosque.community.viewmodel.MosqueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueFinderScreen(
    viewModel: MosqueViewModel,
    onMosqueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Nearby Masajid, 1 = Qibla Compass
    val nearbyMosques by viewModel.filteredNearbyMosques.collectAsState()
    val selectedRadius by viewModel.selectedRadius.collectAsState()
    val selectedMethod by viewModel.selectedCalculationMethod.collectAsState()
    val qiblaInfo by viewModel.qiblaInfo.collectAsState()

    var showMethodPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mosque Finder & Qibla",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Springfield, IL • Method: ${selectedMethod.title}",
                            fontSize = 12.sp,
                            color = SageAccent
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMethodPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Calculation Method",
                            tint = SoftGold
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs: Nearby Masajid vs Qibla Direction
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MidnightInk,
                contentColor = SoftGold,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SoftGold,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Nearby Masajid",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) SoftGold else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Qibla Compass",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) SoftGold else TextSecondary
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // NEARBY MOSQUES TAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Radius filter chips
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Radius:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            DistanceRadius.values().forEach { radius ->
                                val isSelected = radius == selectedRadius
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectRadius(radius) },
                                    label = { Text(radius.label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MidnightCardSurface,
                                        selectedContainerColor = SoftGold,
                                        labelColor = TextPrimary,
                                        selectedLabelColor = MidnightInk
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }

                    // List of Nearby Mosques
                    items(items = nearbyMosques, key = { it.id }) { mosque ->
                        FinderMosqueCard(
                            mosque = mosque,
                            onMosqueClick = { onMosqueClick(mosque.id) },
                            onFollowClick = { viewModel.toggleFollowMosque(mosque.id) }
                        )
                    }
                }
            } else {
                // QIBLA COMPASS TAB WITH REAL SENSOR FUSION
                val context = androidx.compose.ui.platform.LocalContext.current
                androidx.compose.runtime.DisposableEffect(Unit) {
                    val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as? android.hardware.SensorManager
                    val rotationVectorSensor = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR)
                    val accelerometer = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
                    val magnetometer = sensorManager?.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD)

                    val rMatrix = FloatArray(9)
                    val orientation = FloatArray(3)
                    val lastAcc = FloatArray(3)
                    val lastMag = FloatArray(3)
                    var hasAcc = false
                    var hasMag = false

                    val listener = object : android.hardware.SensorEventListener {
                        override fun onSensorChanged(event: android.hardware.SensorEvent) {
                            if (event.sensor.type == android.hardware.Sensor.TYPE_ROTATION_VECTOR) {
                                android.hardware.SensorManager.getRotationMatrixFromVector(rMatrix, event.values)
                                android.hardware.SensorManager.getOrientation(rMatrix, orientation)
                                var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                                azimuth = (azimuth + 360f) % 360f
                                viewModel.updateCompassHeading(azimuth)
                            } else if (event.sensor.type == android.hardware.Sensor.TYPE_ACCELEROMETER) {
                                System.arraycopy(event.values, 0, lastAcc, 0, 3)
                                hasAcc = true
                                if (hasMag) updateFromAccMag()
                            } else if (event.sensor.type == android.hardware.Sensor.TYPE_MAGNETIC_FIELD) {
                                System.arraycopy(event.values, 0, lastMag, 0, 3)
                                hasMag = true
                                if (hasAcc) updateFromAccMag()
                            }
                        }

                        private fun updateFromAccMag() {
                            if (android.hardware.SensorManager.getRotationMatrix(rMatrix, null, lastAcc, lastMag)) {
                                android.hardware.SensorManager.getOrientation(rMatrix, orientation)
                                var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                                azimuth = (azimuth + 360f) % 360f
                                viewModel.updateCompassHeading(azimuth)
                            }
                        }

                        override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
                    }

                    if (rotationVectorSensor != null) {
                        sensorManager.registerListener(listener, rotationVectorSensor, android.hardware.SensorManager.SENSOR_DELAY_UI)
                    } else {
                        accelerometer?.let { sensorManager?.registerListener(listener, it, android.hardware.SensorManager.SENSOR_DELAY_UI) }
                        magnetometer?.let { sensorManager?.registerListener(listener, it, android.hardware.SensorManager.SENSOR_DELAY_UI) }
                    }

                    onDispose {
                        sensorManager?.unregisterListener(listener)
                    }
                }

                QiblaCompassView(
                    qiblaInfo = qiblaInfo,
                    onRotateDemo = { delta ->
                        viewModel.updateCompassHeading((qiblaInfo.currentHeading + delta) % 360f)
                    }
                )
            }
        }

        // Calculation Method Selection Dialog
        if (showMethodPicker) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showMethodPicker = false },
                title = {
                    Text(
                        text = "Prayer Calculation Method",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CalculationMethod.values().forEach { method ->
                            val isSelected = method == selectedMethod
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectCalculationMethod(method)
                                        showMethodPicker = false
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MidnightElevated else MidnightCardSurface
                                ),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, SoftGold) else null
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = method.title,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SoftGold else TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = method.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { showMethodPicker = false }) {
                        Text("Done", color = SoftGold)
                    }
                },
                containerColor = MidnightCardSurface
            )
        }
    }
}

@Composable
fun FinderMosqueCard(
    mosque: Mosque,
    onMosqueClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onMosqueClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightCardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = mosque.imageUrl,
                contentDescription = mosque.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mosque.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SageAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${mosque.distanceMiles} mi • Next: Asr ${mosque.prayerTimes.asrIqamah}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${mosque.followerCount} community members",
                    fontSize = 11.sp,
                    color = SoftGold,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (mosque.isFollowed) {
                OutlinedButton(
                    onClick = onFollowClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SageAccent),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(SageAccent, SageAccent))
                    ),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Following", fontSize = 11.sp)
                }
            } else {
                FilledTonalButton(
                    onClick = onFollowClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SoftGold,
                        contentColor = MidnightInk
                    ),
                    modifier = Modifier.height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Text("+ Follow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QiblaCompassView(
    qiblaInfo: com.mosque.community.data.models.QiblaInfo,
    onRotateDemo: (Float) -> Unit
) {
    val animatedRotation by animateFloatAsState(
        targetValue = qiblaInfo.currentHeading,
        animationSpec = tween(durationMillis = 350),
        label = "compassRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Location Details
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (qiblaInfo.isAligned) SageContainer else SoftGoldContainer)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NearMe,
                    contentDescription = null,
                    tint = if (qiblaInfo.isAligned) SageAccent else SoftGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (qiblaInfo.isAligned) "Facing Makkah (Qibla Aligned)" else "Turn Phone to Align",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (qiblaInfo.isAligned) SageAccent else SoftGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${qiblaInfo.qiblaBearing.toInt()}° ENE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SoftGold
            )
            Text(
                text = "${qiblaInfo.city} ➔ Ka'bah, Makkah (${qiblaInfo.distanceToMakkahMiles} miles)",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Compass Rose Canvas Dial
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(MidnightCardSurface),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(240.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                // Outer circle border
                drawCircle(
                    color = MidnightOutline,
                    radius = radius,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Secondary soft gold inner track
                drawCircle(
                    color = SoftGold.copy(alpha = 0.2f),
                    radius = radius - 15.dp.toPx(),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Draw rotating compass needle
                rotate(degrees = -animatedRotation + qiblaInfo.qiblaBearing, pivot = center) {
                    // Qibla Pointer (pointing to Makkah in Soft Gold)
                    val needlePath = Path().apply {
                        moveTo(center.x, center.y - radius + 25.dp.toPx())
                        lineTo(center.x - 14.dp.toPx(), center.y)
                        lineTo(center.x + 14.dp.toPx(), center.y)
                        close()
                    }
                    drawPath(needlePath, color = SoftGold)

                    // South Pointer (Subtle Muted)
                    val southNeedle = Path().apply {
                        moveTo(center.x, center.y + radius - 25.dp.toPx())
                        lineTo(center.x - 12.dp.toPx(), center.y)
                        lineTo(center.x + 12.dp.toPx(), center.y)
                        close()
                    }
                    drawPath(southNeedle, color = MidnightOutline)
                }

                // Center pivot cap
                drawCircle(color = MidnightInk, radius = 10.dp.toPx())
                drawCircle(color = SoftGold, radius = 6.dp.toPx())
            }
        }

        // Calibration / Testing Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Hardware Compass Active • Keep device flat away from magnetic interference",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { onRotateDemo(-15f) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline)
                ) {
                    Text("Rotate -15°", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { onRotateDemo(15f) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MidnightOutline)
                ) {
                    Text("Rotate +15°", fontSize = 12.sp)
                }
            }
        }
    }
}
