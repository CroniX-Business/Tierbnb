package com.ruki.tierbnb.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import com.ruki.tierbnb.R

sealed class CarItems(var name: String, var icon: Int, var order: Int) {
    object All : CarItems(
        name = "All",
        icon = R.drawable.ic_all_cars,
        order = 1,
    )
    object Near : CarItems(
        name = "Near",
        icon = R.drawable.ic_location_svgrepo_com,
        order = 2,
    )

    object Luxury : CarItems(
        name = "Luxury",
        icon = R.drawable.ic_diamond_svgrepo_com,
        order = 3,
    )

    object Audi : CarItems(
        name = "Audi",
        icon = R.drawable.ic_audi,
        order = 4,
    )

    object BMW : CarItems(
        name = "BMW",
        icon = R.drawable.ic_bmw,
        order = 5,
    )

    object  Ford : CarItems(
        name = "Ford",
        icon = R.drawable.ic_ford_logo,
        order = 6,
    )
}