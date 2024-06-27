package com.ruki.tierbnb.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.ruki.tierbnb.view_models.CarViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.ruki.tierbnb.R
import com.ruki.tierbnb.models.Car
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.ui.theme.GrayBackground
import com.ruki.tierbnb.ui.theme.LightBlue
import com.ruki.tierbnb.view_models.UserViewModel

@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    userViewModel: UserViewModel,
    navController: NavHostController,
    carViewModel: CarViewModel
) {
    val context = LocalContext.current

    val userAuth = auth.currentUser

    userViewModel.fetchUser()
    val user by userViewModel.user.collectAsState()

    var car by remember { mutableStateOf<Car?>(null) }
    val cars by carViewModel.cars.collectAsState()

    car = cars.find { it.id == user?.reservedCar?.carId }
    println("PEDER: $car")


    var newPassword by remember { mutableStateOf(user?.password) }
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


    user?.let {
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
                Text(text = it.name,
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
                Text(text = it.email, fontSize = 20.sp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = newPassword!!,
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
                    onClick = { userAuth?.updatePassword(newPassword!!) }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            car?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 8.dp)
                        .border(
                            width = 1.dp,
                            color = GrayBackground,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .background(GrayBackground)
                        .clickable(onClick = {
                            navController.navigate(NavigationItem.CarDetails.createRoute(it.id))}),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Iznajmljeni auto",
                            fontSize = 16.sp,
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SubcomposeAsyncImage(
                                        model = it.transformedImages[0],
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .border(
                                                width = 2.dp,
                                                color = Color.LightGray,
                                                shape = RoundedCornerShape(15.dp)
                                            )
                                            .clip(RoundedCornerShape(15.dp)),
                                        contentScale = ContentScale.Crop,
                                        loading = { CircularProgressIndicator(
                                            modifier = Modifier.then(Modifier.size(12.dp))
                                        ) },
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "${car?.type} ${it.name} | ${it.year}",
                                    fontSize = 16.sp,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${it.city} | ${it.dealership}",
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate(NavigationItem.LoginScreen.route)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .width(300.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray),
                ) {
                    Text(
                        color = Color.Black,
                        text = "ODJAVI SE"
                    )
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(150.dp, 150.dp),
                color = LightBlue
            )
        }
    }
}