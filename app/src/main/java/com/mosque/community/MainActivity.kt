package com.mosque.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.mosque.community.data.repository.MosqueRepository
import com.mosque.community.ui.navigation.MosqueBottomNavigation
import com.mosque.community.ui.navigation.Screen
import com.mosque.community.ui.screens.admin.AdminPostEventScreen
import com.mosque.community.ui.screens.event.EventDetailScreen
import com.mosque.community.ui.screens.finder.MosqueFinderScreen
import com.mosque.community.ui.screens.home.HomeScreen
import com.mosque.community.ui.screens.profile.MosqueProfileScreen
import com.mosque.community.ui.screens.profile.UserProfileScreen
import com.mosque.community.ui.screens.schedule.MyScheduleScreen
import com.mosque.community.ui.screens.speakers.SpeakerDirectoryScreen
import com.mosque.community.ui.screens.speakers.SpeakerProfileScreen
import com.mosque.community.ui.theme.MidnightInk
import com.mosque.community.ui.theme.MosqueCommunityTheme
import com.mosque.community.viewmodel.MosqueViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var crashDetails: String? = null
        var viewModelInstance: MosqueViewModel? = null

        try {
            MosqueRepository.initialize(this)
            com.mosque.community.util.notification.NotificationHelper.createNotificationChannels(this)
            viewModelInstance = MosqueViewModel(MosqueRepository.getInstance())
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "Startup initialization failed", t)
            crashDetails = "CRASH ON STARTUP:\n${t.javaClass.simpleName}: ${t.message}\n\n${android.util.Log.getStackTraceString(t)}"
        }

        try {
            setContent {
                MosqueCommunityTheme {
                    if (crashDetails != null || viewModelInstance == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = crashDetails ?: "ViewModel could not be initialized",
                                color = Color(0xFFFF6B6B),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        MosqueApp(viewModel = viewModelInstance!!)
                    }
                }
            }
        } catch (t: Throwable) {
            android.util.Log.e("MainActivity", "Fatal Compose initialization error", t)
            val intent = android.content.Intent(this, CrashReportActivity::class.java).apply {
                putExtra("crash_details", "COMPOSE INITIALIZATION CRASH:\n${t.javaClass.name}: ${t.message}\n\n${android.util.Log.getStackTraceString(t)}")
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}

@Composable
fun MosqueApp(
    navController: NavHostController = rememberNavController(),
    viewModel: MosqueViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar on primary tabs
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MosqueBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Discover.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        containerColor = MidnightInk,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Discover.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Tab 1: Discover (Events Feed)
            composable(Screen.Discover.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onMosqueClick = { mosqueId ->
                        navController.navigate(Screen.mosqueProfile(mosqueId))
                    },
                    onEventClick = { eventId ->
                        navController.navigate(Screen.eventDetail(eventId))
                    }
                )
            }

            // Tab 2: Mosques & Communities Directory
            composable(Screen.Mosques.route) {
                MosqueFinderScreen(
                    viewModel = viewModel,
                    onMosqueClick = { mosqueId ->
                        navController.navigate(Screen.mosqueProfile(mosqueId))
                    }
                )
            }

            // Tab 3: Speakers & Scholars Directory
            composable(Screen.Speakers.route) {
                SpeakerDirectoryScreen(
                    viewModel = viewModel,
                    onSpeakerClick = { speakerId ->
                        navController.navigate(Screen.speakerProfile(speakerId))
                    }
                )
            }

            // Tab 4: My Schedule (Personal RSVPs, Saved Lectures & Reminders)
            composable(Screen.MySchedule.route) {
                MyScheduleScreen(
                    viewModel = viewModel,
                    onNavigateToDiscover = {
                        navController.navigate(Screen.Discover.route)
                    },
                    onRequireLogin = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onEventClick = { eventId ->
                        navController.navigate(Screen.eventDetail(eventId))
                    }
                )
            }

            // Tab 5: Profile & Settings (Account, Admin Tools, Qibla)
            composable(Screen.Profile.route) {
                UserProfileScreen(
                    viewModel = viewModel,
                    onNavigateToPostEvent = {
                        navController.navigate(Screen.POST_EVENT_ROUTE)
                    },
                    onNavigateToQibla = {
                        navController.navigate(Screen.QIBLA_ROUTE)
                    },
                    onMosqueClick = { mosqueId ->
                        navController.navigate(Screen.mosqueProfile(mosqueId))
                    },
                    onSpeakerClick = { speakerId ->
                        navController.navigate(Screen.speakerProfile(speakerId))
                    }
                )
            }

            // Sub-screen: Event Detail with Deep Link support
            composable(
                route = Screen.EVENT_DETAIL_ROUTE,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://muslimlink.app/event/{eventId}" },
                    navDeepLink { uriPattern = "muslimlink://event/{eventId}" }
                )
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
                EventDetailScreen(
                    eventId = eventId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onMosqueClick = { mosqueId ->
                        navController.navigate(Screen.mosqueProfile(mosqueId))
                    },
                    onSpeakerClick = { speakerId ->
                        navController.navigate(Screen.speakerProfile(speakerId))
                    }
                )
            }

            // Sub-screen: Speaker Profile
            composable(
                route = Screen.SPEAKER_PROFILE_ROUTE,
                arguments = listOf(navArgument("speakerId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://muslimlink.app/speaker/{speakerId}" },
                    navDeepLink { uriPattern = "muslimlink://speaker/{speakerId}" }
                )
            ) { backStackEntry ->
                val speakerId = backStackEntry.arguments?.getString("speakerId") ?: ""
                SpeakerProfileScreen(
                    speakerId = speakerId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onEventClick = { eventId ->
                        navController.navigate(Screen.eventDetail(eventId))
                    }
                )
            }

            // Sub-screen: Post an Event (Admin Sheet/Screen)
            composable(Screen.POST_EVENT_ROUTE) {
                AdminPostEventScreen(
                    viewModel = viewModel,
                    onEventCreated = {
                        navController.popBackStack()
                    }
                )
            }

            // Sub-screen: Mosque Profile (3-Column Timetable & Amenities)
            composable(
                route = Screen.MOSQUE_PROFILE_ROUTE,
                arguments = listOf(navArgument("mosqueId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://muslimlink.app/mosque/{mosqueId}" },
                    navDeepLink { uriPattern = "muslimlink://mosque/{mosqueId}" }
                )
            ) { backStackEntry ->
                val mosqueId = backStackEntry.arguments?.getString("mosqueId") ?: ""
                MosqueProfileScreen(
                    mosqueId = mosqueId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Sub-screen: Qibla Compass (Supplementary Tool)
            composable(Screen.QIBLA_ROUTE) {
                MosqueFinderScreen(
                    viewModel = viewModel,
                    onMosqueClick = { mosqueId ->
                        navController.navigate(Screen.mosqueProfile(mosqueId))
                    }
                )
            }
        }
    }
}
