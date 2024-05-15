package com.ruki.tierbnb.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object HomeScreen : NavigationItem(
        route = "main_screen",
        icon = Icons.Outlined.Home,
        title = "Main"
    )
    object Map : NavigationItem(
        route = "map_screen",
        icon = Icons.Outlined.LocationOn,
        title = "Map"
    )

    object Profile : NavigationItem(
        route = "profile_screen",
        icon = Icons.Outlined.Person,
        title = "Profile"
    )
}