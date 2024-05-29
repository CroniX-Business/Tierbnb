package com.ruki.tierbnb.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ruki.tierbnb.R
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.ui.theme.GrayBackground
import com.ruki.tierbnb.ui.theme.LightBlue
import java.util.Locale
import com.ruki.tierbnb.view_models.CarViewModel

@Composable
fun MapScreen(
    navController: NavController,
    fusedLocationClient: FusedLocationProviderClient
) {
    var cityName by remember { mutableStateOf("Unknown") }
    var userLatitude by remember { mutableDoubleStateOf(0.0) }
    var userLongitude by remember { mutableDoubleStateOf(0.0) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val context = LocalContext.current

    val carViewModel: CarViewModel = viewModel()
    val cars by carViewModel.cars.collectAsState()

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                userLatitude = location.latitude
                userLongitude = location.longitude

                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        cityName = address.locality

                    }
                }
            }
        }
        .addOnFailureListener {
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight - 55.dp)
            .background(GrayBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val defaultLocation = LatLng(45.815399, 15.966568)
            val userLocation = LatLng(userLatitude, userLongitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(defaultLocation, 8f)
            }
            val uiSettings by remember {
                mutableStateOf(
                    MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = true,
                    )
                )
            }
            GoogleMap(
                properties = MapProperties(isMyLocationEnabled = true),
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                onMyLocationButtonClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 15f)
                    true
                }
            ) {
                cars.forEach { car ->
                    MarkerInfoWindow(
                        state = MarkerState(position = LatLng(car.latitude, car.longitude)),
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon),
                        onInfoWindowLongClick = {navController.navigate(NavigationItem.CarDetails.createRoute(car.id))}
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .border(
                                    BorderStroke(1.dp, Color.Black),
                                    RoundedCornerShape(10)
                                )
                                .clip(RoundedCornerShape(10))
                                .background(LightBlue)
                                .padding(20.dp)
                        ) {
                            Text("${car.type} ${car.name}", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                text = buildString {
                                    val dailyPriceDouble = car.dailyPrice.toDoubleOrNull() ?: 0.0
                                    val monthlyPrice = dailyPriceDouble * 20
                                    append("Day - ${car.dailyPrice}€ | Month - ${monthlyPrice}€") },
                                fontWeight = FontWeight.Medium,
                                color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    navController.navigate(NavigationItem.CarDetails.createRoute(car.id))
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(text = "Pritisni duže za info")
                            }
                        }
                    }
                }
            }
        }
    }
}
