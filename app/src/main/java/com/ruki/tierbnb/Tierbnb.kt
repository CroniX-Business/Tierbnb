package com.ruki.tierbnb

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.ruki.tierbnb.costume_modifier.topBorder
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.screens.LoginScreen
import com.ruki.tierbnb.screens.RegisterScreen
import com.ruki.tierbnb.screens.MainScreen
import com.ruki.tierbnb.ui.theme.BottomBarAnimationTheme
import com.ruki.tierbnb.screens.LoadingScreen
import com.ruki.tierbnb.ui.theme.LightBlue

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val db = Firebase.firestore

            val auth: FirebaseAuth = Firebase.auth
            val navController = rememberNavController()

            LaunchedEffect(key1 = auth.currentUser) {
                //delay(3000)
                auth.currentUser?.let {
                    navController.navigate(NavigationItem.HomeScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                    }
                } ?: run {
                    navController.navigate("login_screen") {
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
            }
            BottomBarAnimationApp(navController = navController, auth = auth)
        }
    }
}

@Composable
fun BottomBarAnimationApp(navController: NavHostController, auth: FirebaseAuth) {
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    BottomBarAnimationTheme {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        when (navBackStackEntry?.destination?.route) {
            "main_screen" -> {
                bottomBarState.value = true
                topBarState.value = true
            }
            "map_screen" -> {
                bottomBarState.value = true
                topBarState.value = false
            }
            "profile_screen" -> {
                bottomBarState.value = true
                topBarState.value = false
            }
        }

        com.google.accompanist.insets.ui.Scaffold(
            bottomBar = {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                val shouldShowBottomBar = currentRoute in listOf("main_screen", "map_screen", "profile_screen")

                if (shouldShowBottomBar) {
                    BottomBar(
                        navController = navController,
                        bottomBarState = bottomBarState
                        )
                }
            },
            /*topBar = {
                // Determine visibility based on the current route
                val shouldShowTopBar = navController.currentBackStackEntry?.destination?.route !in listOf("loading_screen", "login_screen", "register_screen")

                if (shouldShowTopBar) {
                    TopBar(
                        navController = navController,
                        topBarState = topBarState
                    )
                }
            },*/
            content = {
                NavHost(
                    navController = navController,
                    startDestination = "loading_screen",
                ) {
                    composable("loading_screen") {
                        LoadingScreen()
                    }
                    composable(NavigationItem.HomeScreen.route) {
                        MainScreen(navController = navController, auth = auth)
                    }
                    composable(NavigationItem.Map.route) {
                    }
                    composable(NavigationItem.Profile.route) {
                    }
                    composable("login_screen") {
                        LoginScreen(navController = navController, auth = auth)
                    }
                    composable("register_screen") {
                        RegisterScreen(navController = navController, auth = auth)
                    }
                }
            }
        )
    }
}

@Composable
fun BottomBar(navController: NavController, bottomBarState: MutableState<Boolean>) {
    val items = listOf(
        NavigationItem.HomeScreen,
        NavigationItem.Map,
        NavigationItem.Profile
    )

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            BottomNavigation{
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val selected = currentRoute == item.route

                    BottomNavigationItem(
                        icon = {
                            Icon(imageVector = item.icon,
                                contentDescription = "",
                                tint = if (selected) LightBlue else Color.Black)
                        },
                        label = { Text(text = item.title, color = if (selected) LightBlue else Color.Black) },
                        selected = selected,
                        modifier = Modifier
                            .background(Color.White)
                            .topBorder(color = Color.Gray.copy(0.3F)),
                        onClick = {
                            navController.navigate(item.route) {
                            }
                        }
                    )
                }
            }
        }
    )
}

/*@Composable
fun TopBar(navController: NavController, topBarState: MutableState<Boolean>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val title: String = when (currentRoute) {
        "main_screen" -> "Cars"
        "map_screen" -> "Map"
        "profile_screen" -> "Profile"
        else -> "Tierbnb"
    }

    AnimatedVisibility(
        visible = topBarState.value,
        //currentRoute in listOf("main_screen", "map_screen", "profile_screen"),
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        content = {
            TopAppBar(
                modifier = Modifier
                    .height(150.dp),
                backgroundColor = Color.White,
                title = { Text(text = title) },
            )
        }
    )
}*/

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