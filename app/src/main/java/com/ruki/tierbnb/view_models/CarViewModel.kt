package com.ruki.tierbnb.view_models

import com.ruki.tierbnb.models.Car
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CarViewModel : ViewModel() {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> get() = _cars

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchCars()
    }

    fun fetchCars() {
        viewModelScope.launch {
            try {
                val result = db.collection("cars").get().await()
                val carList = result.map { document ->
                    val car = document.toObject(Car::class.java).apply { id = document.id }
                    car.transformedImages = transformImageUrls(car.images)
                    car
                }
                _cars.value = carList
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun transformImageUrls(images: List<String>): List<String> {
        return images.map { imageUrl ->
            try {
                val storageReference = Firebase.storage.getReferenceFromUrl(imageUrl)
                val uri = storageReference.downloadUrl.await()
                uri.toString()
            } catch (e: Exception) {
                ""
            }
        }.filter { it.isNotEmpty() }
    }
}

/*class CarViewModel : ViewModel() {

    private val _cars = MutableStateFlow<List<com.ruki.tierbnb.models.Car>>(emptyList())
    val cars: StateFlow<List<com.ruki.tierbnb.models.Car>> get() = _cars

    private val db = FirebaseFirestore.getInstance()

    init {
        fetchCars()
    }

    private fun fetchCars() {
        viewModelScope.launch {
            db.collection("cars")
                .get()
                .addOnSuccessListener { result ->
                    val carList = result.map { document ->
                        val car = document.toObject(com.ruki.tierbnb.models.Car::class.java)
                        car.id = document.id
                        car
                    }
                    _cars.value = carList
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }
}*/
