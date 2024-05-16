package com.ruki.tierbnb.models

import com.ruki.tierbnb.R

sealed class CarItems(var name: String, var icon: Int) {
    object Near : CarItems(
        name = "Near",
        icon = R.drawable.ic_location_svgrepo_com,
    )

    object Luxury : CarItems(
        name = "Luxury",
        icon = R.drawable.ic_diamond_svgrepo_com,
    )

    object Audi : CarItems(
        name = "Audi",
        icon = R.drawable.ic_audi,
    )

    object BMW : CarItems(
        name = "BMW",
        icon = R.drawable.ic_bmw,
    )

    object  Ford : CarItems(
        name = "Ford",
        icon = R.drawable.ic_ford_logo,
    )
}