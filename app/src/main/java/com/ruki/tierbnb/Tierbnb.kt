package com.ruki.tierbnb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.accompanist.insets.ui.TopAppBar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ruki.tierbnb.screens.LoginScreen
import com.ruki.tierbnb.screens.RegisterScreen
import com.ruki.tierbnb.screens.MainScreen
import com.ruki.tierbnb.ui.theme.BottomBarAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val auth: FirebaseAuth = Firebase.auth
            val navController = rememberNavController()

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
            BottomBarAnimationApp(navController = navController, auth = auth)
        }
    }
}

@Composable
fun BottomBarAnimationApp(navController: NavHostController, auth: FirebaseAuth,) {

    // State of bottomBar, set state to false, if current page route is "car_details"
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    // State of topBar, set state to false, if current page route is "car_details"
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    BottomBarAnimationTheme {
        // Subscribe to navBackStackEntry, required to get current route
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // Control TopBar and BottomBar
        when (navBackStackEntry?.destination?.route) {
            "cars" -> {
                // Show BottomBar and TopBar
                bottomBarState.value = true
                topBarState.value = true
            }
            "bikes" -> {
                // Show BottomBar and TopBar
                bottomBarState.value = true
                topBarState.value = true
            }
            "settings" -> {
                // Show BottomBar and TopBar
                bottomBarState.value = true
                topBarState.value = true
            }
            "car_details" -> {
                // Hide BottomBar and TopBar
                bottomBarState.value = false
                topBarState.value = false
            }
        }

        com.google.accompanist.insets.ui.Scaffold(
            bottomBar = {
                // Extract the current route from the navigation controller
                val currentRoute = navController.currentBackStackEntry?.destination?.route

                // Determine visibility based on the current route
                val shouldShowBottomBar = currentRoute !in listOf("loading_screen", "login_screen", "register_screen")

                if (shouldShowBottomBar) {
                    BottomBar(
                        navController = navController,
                        bottomBarState = bottomBarState
                        )
                }
            },
            topBar = {
                // Determine visibility based on the current route
                val shouldShowTopBar = navController.currentBackStackEntry?.destination?.route !in listOf("loading_screen", "login_screen", "register_screen")

                if (shouldShowTopBar) {
                    TopBar(
                        navController = navController,
                        topBarState = topBarState
                    )
                }
            },
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
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    BottomNavigationItem(
                        icon = {
                            Icon(imageVector = item.icon, contentDescription = "")
                        },
                        label = { Text(text = item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                // Customize navigation options here if needed
                            }
                        }
                    )
                }
            }
        }
    )
}

@Composable
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
        visible = currentRoute !in listOf("loading_screen", "login_screen", "register_screen"),
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        content = {
            TopAppBar(
                title = { Text(text = title) },
            )
        }
    )
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