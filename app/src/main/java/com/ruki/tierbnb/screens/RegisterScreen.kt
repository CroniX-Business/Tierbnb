package com.ruki.tierbnb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ruki.tierbnb.BackgroundImage
import com.ruki.tierbnb.R
import com.ruki.tierbnb.services.register
import com.ruki.tierbnb.ui.theme.LightBlue

@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRep by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisibleRep by remember { mutableStateOf(false) }

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
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Start),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.eye_show_svgrepo_com else R.drawable.eye_hide_svgrepo_com

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = painterResource(image), contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = Color.White,
                )
            )
            OutlinedTextField(
                value = passwordRep,
                onValueChange = { passwordRep = it },
                label = { Text("Lozinka") },
                visualTransformation = if (passwordVisibleRep) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.padding(vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Start),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibleRep) R.drawable.eye_show_svgrepo_com else R.drawable.eye_hide_svgrepo_com

                    IconButton(onClick = { passwordVisibleRep = !passwordVisibleRep }) {
                        Icon(painter = painterResource(image), contentDescription = if (passwordVisibleRep) "Hide password" else "Show password")
                    }
                },
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