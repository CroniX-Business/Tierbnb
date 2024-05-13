package com.ruki.tierbnb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ruki.tierbnb.ui.theme.TierbnbTheme
import com.ruki.tierbnb.auth.LoginScreen
import com.ruki.tierbnb.auth.RegisterScreen
import com.ruki.tierbnb.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TierbnbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    val auth: FirebaseAuth = Firebase.auth

                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        println(currentUser.email)
                    }

                    LaunchedEffect(key1 = auth.currentUser) {
                        auth.currentUser?.let {
                            navController.navigate("main_screen") {
                                popUpTo(navController.graph.startDestinationId)
                            }
                        } ?: run {
                            navController.navigate("login_screen") {
                                popUpTo(navController.graph.startDestinationId)
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = "loading_screen") {
                        composable("loading_screen") {
                            LoadingScreen()
                        }
                        composable("main_screen") {
                            MainScreen(navController = navController, auth = auth)
                        }
                        composable("login_screen") {
                            LoginScreen(navController = navController, auth = auth)
                        }
                        composable("register_screen") {
                            RegisterScreen(navController = navController, auth = auth)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BackgroundImage(modifier: Modifier, @DrawableRes imageResource: Int) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.8F
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(100.dp),
            color = Color.Cyan
        )
    }
}


/*@Composable
fun MainScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment =
    Alignment.Center) {
        BackgroundImage(modifier = Modifier.fillMaxSize(), imageResource = R.drawable.login_background)
        LoginScreen(navController = navController)
    }
}*/