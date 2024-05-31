package com.ruki.tierbnb.models

data class User(
    var id: String = "",
    var email: String = "",
    var name: String = "",
    var password: String = "",
    val reservedCar: ReservedCar? = null
)

data class ReservedCar(
    val carId: String = "",
    val firstDate: String = "",
    val lastDate: String = ""
)