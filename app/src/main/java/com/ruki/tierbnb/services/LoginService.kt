package com.ruki.tierbnb.services

import android.content.Context
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ruki.tierbnb.components.showToast
import com.ruki.tierbnb.view_models.CarViewModel
import com.ruki.tierbnb.view_models.UserViewModel

fun login(
    email: String,
    password: String,
    auth: FirebaseAuth,
    navController: NavController,
    userViewModel: UserViewModel,
    carViewModel: CarViewModel,
    context: Context,
) {
    if (email.isEmpty() || password.isEmpty()) {
        showToast(context, "Popunite sva polja.")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userViewModel.fetchUser()
                carViewModel.fetchCars()
                navController.navigate("main_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            } else {
                val errorText = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "Nevažeći korisnik. Provjerite svoju e-poštu."
                    is FirebaseAuthInvalidCredentialsException -> "Nevažeće vjerodajnice. Provjerite svoju e-poštu i lozinku."
                    is FirebaseAuthUserCollisionException -> "Korisnik s ovom e-poštom već postoji."
                    else -> "Provjera autentičnosti nije uspjela. Molimo pokušajte ponovo kasnije."
                }
                showToast(context, errorText)
                navController.navigate("login_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            }
        }
}