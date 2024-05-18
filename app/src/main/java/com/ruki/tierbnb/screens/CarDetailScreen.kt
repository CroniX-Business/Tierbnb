package com.ruki.tierbnb.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import Car
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.ruki.tierbnb.models.NavigationItem

@Composable
fun CarDetailsScreen(carId: String, navController: NavController) {
    val db = Firebase.firestore
    var car by remember { mutableStateOf<Car?>(null) }

    LaunchedEffect(carId) {
        db.collection("cars").document(carId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    car = document.toObject(Car::class.java)
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    car?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = it.name)
            Text(text = "Type: ${it.type}")
            Text(text = "Year: ${it.year}")
            Text(text = "Price: ${it.price}")
            // Display other car details as needed
            ClickableText(
                text = AnnotatedString("Nazad"),
                onClick = {navController.navigate(NavigationItem.HomeScreen.route)},
            )
        }
    } ?: run {
        // Display a loading indicator or placeholder
        CircularProgressIndicator()
    }
}