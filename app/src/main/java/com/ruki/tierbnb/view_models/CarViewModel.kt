package com.ruki.tierbnb.view_models

import Car
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarViewModel : ViewModel() {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> get() = _cars

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
                        document.toObject(Car::class.java)
                    }
                    _cars.value = carList
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }
}
