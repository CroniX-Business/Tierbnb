package com.ruki.tierbnb.auth

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ruki.tierbnb.BackgroundImage
import com.ruki.tierbnb.R
import com.ruki.tierbnb.showToast
import com.ruki.tierbnb.ui.theme.LightBlue

@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRep by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        BackgroundImage(modifier = Modifier.fillMaxSize(), imageResource = R.drawable.login_background)
        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart),
            onClick = { navController.navigate("login_screen") }) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color.LightGray.copy(0.5F))
                .padding(20.dp)
        ) {
            Text(
                text = "REGISTRACIJA",
                fontSize = 20.sp,
                lineHeight = 61.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = Color.White,
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Lozinka") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = Color.White,
                )
            )
            OutlinedTextField(
                value = passwordRep,
                onValueChange = { passwordRep = it },
                label = { Text("Ponovite lozinku") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = Color.White,
                )
            )
            Button(
                onClick = {
                    register(email, password, passwordRep, auth, navController, context)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue, contentColor = Color.White)
            ) {
                Text(text = "Registriraj se")
            }
        }
    }
}

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
                navController.navigate("main_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            } else {
                val errorText = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "Invalid user. Please check your email."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid credentials. Please check your email and password."
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
    val userCollection = db.collection("users")
    val userData = hashMapOf(
        "email" to user.email, // Here you can add more fields as needed
        "otherField" to "defaultValue"
    )

    // Create a document in the 'users' collection with the user's ID
    userCollection.document(user.uid)
        .set(userData)
        .addOnSuccessListener {
            // Document creation successful
            println("User data added to Firestore.")
        }
        .addOnFailureListener { e ->
            // Document creation failed
            println("Error adding user data to Firestore: $e")
        }
}