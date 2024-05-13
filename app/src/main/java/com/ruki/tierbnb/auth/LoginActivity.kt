package com.ruki.tierbnb.auth

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.AnnotatedString
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
import com.ruki.tierbnb.BackgroundImage
import com.ruki.tierbnb.R
import com.ruki.tierbnb.showToast
import com.ruki.tierbnb.ui.theme.LightBlue

@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        BackgroundImage(modifier = Modifier.fillMaxSize(), imageResource = R.drawable.login_background)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color.LightGray.copy(0.5F))
                .padding(20.dp)
        ) {
            Text(
                text = "PRIJAVA",
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
            Button(
                onClick = {
                    login(email, password, auth, navController, context)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue, contentColor = Color.White)
            ) {
                Text(text = "Prijavi se")
            }
            ClickableText(
                text = AnnotatedString("Nemaš račun? registriraj se"),
                onClick = {navController.navigate("register_screen")},
                style = TextStyle(
                    color = Color.White,
                )
            )
        }
    }
}

fun login(
    email: String,
    password: String,
    auth: FirebaseAuth,
    navController: NavController,
    context: Context
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
                // Show error message using Toast
                showToast(context, errorText)
                // Navigate back to login screen
                navController.navigate("login_screen") {
                    popUpTo(navController.graph.startDestinationId)
                }
            }
        }
}