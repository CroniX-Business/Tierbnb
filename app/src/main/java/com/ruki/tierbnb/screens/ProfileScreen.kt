package com.ruki.tierbnb.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.auth.FirebaseAuth
import com.ruki.tierbnb.R
import com.ruki.tierbnb.ui.theme.LightBlue

@Composable
fun ProfileScreen(auth: FirebaseAuth) {
    val context = LocalContext.current

    val user = auth.currentUser

    var newPassword by remember { mutableStateOf("*******") }
    var passwordVisible by remember { mutableStateOf(false) }

    val avatars = listOf(
        R.drawable.dog,
        R.drawable.man,
        R.drawable.girl,
        R.drawable.woman,
        R.drawable.gamer,
        R.drawable.teacher,
        R.drawable.african,
    )

    var selectedAvatar by remember {
        mutableIntStateOf(
            context
                .getSharedPreferences("AvatarPrefs", Context.MODE_PRIVATE)
                .getInt("SelectedAvatar", avatars.firstOrNull() ?: avatars.first())
        )
    }
    var isDialogOpen by remember { mutableStateOf(false) }

    if (user != null) {
        val userName = user.email?.substringBefore('@')

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = selectedAvatar),
                    contentDescription = "Selected Avatar",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .clickable { isDialogOpen = true }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "${userName}",
                    fontSize = 30.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isDialogOpen) {
                Dialog(
                    onDismissRequest = { isDialogOpen = false },
                    properties = DialogProperties(dismissOnClickOutside = true)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .width(400.dp)
                            .height(300.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Select Avatar")

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(avatars.size) { index ->
                                    val avatar = avatars[index]
                                    Image(
                                        painter = painterResource(id = avatar),
                                        contentDescription = "Avatar $avatar",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                selectedAvatar = avatar
                                                isDialogOpen = false
                                                context
                                                    .getSharedPreferences(
                                                        "AvatarPrefs",
                                                        Context.MODE_PRIVATE
                                                    )
                                                    .edit()
                                                    .putInt("SelectedAvatar", avatar)
                                                    .apply()
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(text = "${user.email}", fontSize = 20.sp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(text = "Lozinka", color = Color.Black) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(260.dp),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Start),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) R.drawable.eye_show_svgrepo_com else R.drawable.eye_hide_svgrepo_com

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(painter = painterResource(image), contentDescription = if (passwordVisible) "Hide password" else "Show password")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LightBlue,
                        unfocusedBorderColor = Color.Black)
                )
                IconButton(
                    colors = IconButtonDefaults.iconButtonColors(containerColor = LightBlue, contentColor = Color.White),
                    modifier = Modifier
                        .height(60.dp)
                        .width(100.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .padding(start = 25.dp),
                    onClick = { user.updatePassword(newPassword) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    }
}