package com.ruki.tierbnb

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.ruki.tierbnb.costume_modifier.topBorder
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.screens.CarDetailsScreen
import com.ruki.tierbnb.screens.CarReservationScreen
import com.ruki.tierbnb.screens.LoginScreen
import com.ruki.tierbnb.screens.RegisterScreen
import com.ruki.tierbnb.screens.MainScreen
import com.ruki.tierbnb.ui.theme.BottomBarAnimationTheme
import com.ruki.tierbnb.screens.LoadingScreen
import com.ruki.tierbnb.screens.MapScreen
import com.ruki.tierbnb.screens.ProfileScreen
import com.ruki.tierbnb.ui.theme.LightBlue
import com.ruki.tierbnb.view_models.CarViewModel
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationChannel = NotificationChannel(
            "notification_channel_id",
            "Notification name",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        ActivityCompat.requestPermissions(this, permissions, 10)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            val auth: FirebaseAuth = Firebase.auth
            val navController = rememberNavController()
            val carViewModel: CarViewModel = viewModel()

            LaunchedEffect(key1 = auth.currentUser) {
                delay(2500)
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
            BottomBarAnimationApp(navController = navController, auth = auth, fusedLocationClient = fusedLocationClient, carViewModel = carViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomBarAnimationApp(
    navController: NavHostController,
    auth: FirebaseAuth,
    fusedLocationClient: FusedLocationProviderClient,
    carViewModel: CarViewModel
) {
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    BottomBarAnimationTheme {
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        when (navBackStackEntry?.destination?.route) {
            NavigationItem.HomeScreen.route -> {
                bottomBarState.value = true
            }
            NavigationItem.Map.route -> {
                bottomBarState.value = true
            }
            NavigationItem.Profile.route -> {
                bottomBarState.value = true
            }
        }

        com.google.accompanist.insets.ui.Scaffold(
            bottomBar = {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                val shouldShowBottomBar = currentRoute in listOf(NavigationItem.HomeScreen.route, NavigationItem.Map.route, NavigationItem.Profile.route)

                if (shouldShowBottomBar) {
                    BottomBar(
                        navController = navController,
                        bottomBarState = bottomBarState
                        )
                }
            },
            content = {
                NavHost(
                    navController = navController,
                    startDestination = "loading_screen",
                ) {
                    composable(NavigationItem.LoadingScreen.route) {
                        LoadingScreen()
                    }
                    composable(NavigationItem.HomeScreen.route) {
                        MainScreen(navController = navController, fusedLocationClient = fusedLocationClient, carViewModel = carViewModel)
                    }
                    composable(NavigationItem.Map.route) {
                        MapScreen(navController = navController, fusedLocationClient = fusedLocationClient)
                    }
                    composable(NavigationItem.Profile.route) {
                        ProfileScreen(auth = auth)
                    }
                    composable(NavigationItem.LoginScreen.route) {
                        LoginScreen(navController = navController, auth = auth)
                    }
                    composable(NavigationItem.RegisterScreen.route) {
                        RegisterScreen(navController = navController, auth = auth)
                    }
                    composable(NavigationItem.CarDetails.route) { backStackEntry ->
                        val carId = backStackEntry.arguments?.getString("carId")
                        carId?.let {
                            CarDetailsScreen(carId = it, navController = navController, carViewModel = carViewModel)
                        }
                    }
                    composable(NavigationItem.CarReservation.route) { backStackEntry ->
                        val carId = backStackEntry.arguments?.getString("carId")
                        carId?.let {
                            CarReservationScreen(carId = it, navController = navController, carViewModel = carViewModel, auth = auth)
                        }
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
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        content = {
            BottomNavigation{
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    val selected = currentRoute == item.route

                    BottomNavigationItem(
                        icon = {
                            item.icon?.let {
                                Icon(imageVector = it,
                                    contentDescription = "",
                                    tint = if (selected) LightBlue else Color.Black)
                            }
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