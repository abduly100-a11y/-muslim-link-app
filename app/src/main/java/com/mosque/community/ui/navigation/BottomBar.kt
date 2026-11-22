package com.mosque.community.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mosque.community.ui.theme.MidnightElevated
import com.mosque.community.ui.theme.MidnightSurface
import com.mosque.community.ui.theme.SoftGold
import com.mosque.community.ui.theme.SoftGoldContainer
import com.mosque.community.ui.theme.TextPrimary
import com.mosque.community.ui.theme.TextSecondary

@Composable
fun MosqueBottomNavigation(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit
) {
    NavigationBar(
        containerColor = MidnightSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        onNavigateToRoute(screen.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        maxLines = 1,
                        color = if (isSelected) SoftGold else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SoftGold,
                    selectedTextColor = SoftGold,
                    indicatorColor = SoftGoldContainer,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
