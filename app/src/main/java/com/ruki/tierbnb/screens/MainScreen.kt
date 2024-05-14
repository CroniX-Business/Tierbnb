package com.ruki.tierbnb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ruki.tierbnb.R

@Composable
fun MainScreen(navController: NavController, auth: FirebaseAuth) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart),
            onClick = {
                auth.signOut()
                navController.navigate("login_screen")
            }) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                contentDescription = "Back",
                tint = Color.Black
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
                text = "BOK",
                fontSize = 20.sp,
                lineHeight = 61.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

