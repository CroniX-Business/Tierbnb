package com.ruki.tierbnb.models

data class Car(
    var id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var luxury: Boolean = false,
    val name: String = "",
    val dailyPrice: String = "",
    val type: String = "",
    val year: String = "",
    val images: List<String> = emptyList(),
    var transformedImages: List<String> = emptyList(),
    val city: String = "",
    val dealership: String = "",
    val gear: String = "",
    val horsePower: String = "",
    val maxSeat: String = "",
    val minAge: String = "",
)