package com.ruki.tierbnb.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var route: String, var icon: ImageVector?, var title: String) {
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

    object CarDetails : NavigationItem(
        route = "car_details_screen/{carId}",
        icon = null,
        title = "com.ruki.tierbnb.models.Car Details"
    ) {
        fun createRoute(carId: String) = "car_details_screen/$carId"
    }

    object LoadingScreen : NavigationItem(
        route = "loading_screen",
        icon = null,
        title = "Loading Screen"
    )

    object LoginScreen : NavigationItem(
        route = "login_screen",
        icon = null,
        title = "Login Screen"
    )

    object RegisterScreen : NavigationItem(
        route = "register_screen",
        icon = null,
        title = "Register Screen"
    )

    object CarReservation : NavigationItem(
        route = "car_reservation/{carId}",
        icon = null,
        title = "com.ruki.tierbnb.models.Car reservation"
    ) {
        fun createRoute(carId: String) = "car_reservation/$carId"
    }
}