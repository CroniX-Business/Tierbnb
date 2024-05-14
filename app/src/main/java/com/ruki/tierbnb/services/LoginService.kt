package com.ruki.tierbnb.services

import android.content.Context
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.ruki.tierbnb.components.showToast

fun login(
    email: String,
    password: String,
    auth: FirebaseAuth,
    navController: NavController,
    context: Context,
) {
    if (email.isEmpty() || password.isEmpty()) {
        showToast(context, "Popunite sva polja.")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("main_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            } else {
                val errorText = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "Invalid user. Please check your email."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please check your email and password."
                    is FirebaseAuthUserCollisionException -> "User with this email already exists."
                    else -> "Authentication failed. Please try again later."
                }
                showToast(context, errorText)
                navController.navigate("login_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            }
        }
}