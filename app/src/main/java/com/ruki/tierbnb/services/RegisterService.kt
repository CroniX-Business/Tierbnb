package com.ruki.tierbnb.services

import android.content.Context
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.components.showToast

fun register(
    email: String,
    password: String,
    passwordRep: String,
    auth: FirebaseAuth,
    navController: NavController,
    context: Context
) {
    if (email.isEmpty() || password.isEmpty() || passwordRep.isEmpty()) {
        showToast(context, "Popunite sva polja.")
        return
    }

    if (password != passwordRep) {
        showToast(context, "Lozinke se ne podudaraju.")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let { firebaseUser ->
                    createFirestoreCollectionForUser(firebaseUser)
                }
                navController.navigate(NavigationItem.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            } else {
                val errorText = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "Ne postojeći korisnik."
                    is FirebaseAuthInvalidCredentialsException -> "Nevažeći podatci."
                    is FirebaseAuthUserCollisionException -> "User with this email already exists."
                    else -> "Registracija neuspješna. Pokušajte ponovo."
                }
                showToast(context, errorText)
                navController.navigate("register_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            }
        }
}

private fun createFirestoreCollectionForUser(user: FirebaseUser) {
    val db = FirebaseFirestore.getInstance()
    val atIndex = user.email?.indexOf('@')
    val userCollection = db.collection("users")
    val userData = hashMapOf(
        "email" to user.email,
        "name" to atIndex?.let { user.email?.substring(0, it) }
    )

    userCollection.document(user.uid)
        .set(userData)
        .addOnSuccessListener {
            println("User data added to Firestore.")
        }
        .addOnFailureListener { e ->
            println("Error adding user data to Firestore: $e")
        }
}