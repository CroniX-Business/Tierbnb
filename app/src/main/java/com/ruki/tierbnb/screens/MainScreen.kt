package com.ruki.tierbnb.screens

import Car
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.ruki.tierbnb.R
import com.ruki.tierbnb.costume_modifier.bottomBorder
import com.ruki.tierbnb.models.CarItems
import com.ruki.tierbnb.ui.theme.GrayBackground
import com.ruki.tierbnb.ui.theme.LightBlue
import com.ruki.tierbnb.view_models.CarViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun MainScreen(
    navController: NavController,
    auth: FirebaseAuth,
    fusedLocationClient: FusedLocationProviderClient
) {
    var selectedOption by remember { mutableStateOf("Near") }
    var selectedCar by remember { mutableStateOf("") }
    var cityName by remember { mutableStateOf("Unknown") }
    var userLatitude by remember { mutableStateOf(0.0) }
    var userLongitude by remember { mutableStateOf(0.0) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight - 55.dp)
            .background(GrayBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = LightBlue)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = LightBlue),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(onSearch = { query ->
                    // searchViewModel.performSearch(query)
                })

                Spacer(modifier = Modifier.height(16.dp))

                SliderNavigationBar(
                    selectedOption = selectedOption,
                    onOptionSelected = { newOption ->
                        selectedOption = newOption
                    }
                )
            }
        }

        Divider(
            color = Color.Gray,
            thickness = 2.dp
        )

        val context = LocalContext.current

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
                            val address = addresses.get(0)
                            cityName = address.locality

                        }
                    }
                }
            }
            .addOnFailureListener { exception: Exception ->
            }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 16.dp, end = 10.dp)
        ) {
            if (selectedOption == "Near") {
                Text(
                    modifier = Modifier
                        .height(50.dp)
                        .width(130.dp)
                        .wrapContentHeight()
                        .border(
                            width = 2.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(20)
                        )
                        .clip(RoundedCornerShape(20))
                        .background(Color.LightGray),
                    text = cityName,
                    fontSize = 13.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            IconButton(
                onClick = { /* do something */ },
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = "Filter")
            }
        }

        val carViewModel: CarViewModel = viewModel()
        val cars by carViewModel.cars.collectAsState()

        val maxDistanceInKm = 50.0

        val filteredCars = cars.filter { car ->
            when (selectedOption) {
                "Near" -> {
                    val carLatitude = car.latitude
                    val carLongitude = car.longitude
                    val distance = calculateDistanceInKm(userLatitude, userLongitude, carLatitude, carLongitude)
                    distance <= maxDistanceInKm
                }
                "Luxury" -> car.luxury == true
                else -> car.type == selectedOption
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(filteredCars) { index, car ->
                CarItem(
                    car = car,
                    isSelected = car.name == selectedCar,
                    onCarSelected = { /*onCarSelected(car.name)*/ }
                )
            }
        }
    }
}

fun calculateDistanceInKm(
    latUser: Double, lonUser: Double,
    latCar: Double, lonCar: Double
): Double {
    val radius = 6371

    println("USER - Lat: ${latUser} - Long: ${lonUser}")
    println("CAR - Lat: ${latCar} - Long: ${lonCar}")

    val dLat = Math.toRadians(latCar - latUser)
    val dLon = Math.toRadians(lonCar - lonUser)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(latUser)) * cos(Math.toRadians(latCar)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = LightBlue)
            .padding(horizontal = 8.dp)
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search Icon",
                tint = Color.Black,
                modifier = Modifier
                    .padding(start = 8.dp, end = 4.dp)
            )

            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(searchText)
                    }
                ),
                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        Text(
                            text = "Search car models",
                            color = Color.Gray.copy(alpha = 0.5f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp) // Adjust padding as needed
            )
        }
    }
}

@Composable
fun SliderNavigationBar(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
) {
    val options = CarItems::class.sealedSubclasses.mapNotNull { subclass ->
        subclass.objectInstance
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
        ) {
            itemsIndexed(options.reversed()) { index, option ->
                OptionItem(
                    option = option,
                    isSelected = option.name == selectedOption,
                    onOptionSelected = { onOptionSelected(option.name) }
                )
            }
        }
    }
}

@Composable
fun OptionItem(
    option: CarItems,
    isSelected: Boolean,
    onOptionSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onOptionSelected)
            .padding(horizontal = 16.dp)
            .bottomBorder(
                if (isSelected) Color.Black else Color.Transparent,
                yOffSet = -50,
                width = 2.dp
            )
            .width(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(option.icon),
                contentDescription = option.name,
                modifier = Modifier.size(25.dp)
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = option.name,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun SliderCars(
    selectedCar: String,
    onCarSelected: (String) -> Unit,
    carViewModel: CarViewModel = viewModel()
) {
    val cars by carViewModel.cars.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        itemsIndexed(cars) { index, car ->
            CarItem(
                car = car,
                isSelected = car.name == selectedCar,
                onCarSelected = { onCarSelected(car.name) }
            )
        }
    }
}

@Composable
fun CarItem(
    car: Car,
    isSelected: Boolean,
    onCarSelected: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(top = 10.dp)
            .clickable(onClick = onCarSelected)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Replace with AsyncImage or CoilImage to load from URL
            Image(
                painter = painterResource(id = R.drawable.login_background),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
            ) {
                Text(
                    text = "${car.type} ${car.name} | ${car.year}",
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Day - ${car.price} | Month - ${car.price}",
                    textAlign = TextAlign.Start,
                    color = Color.Gray,
                    fontSize = 18.sp,
                )
                // Add other car details if needed
            }
        }
    }
}
    /*Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .clickable(onClick = onCarSelected)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(30.dp))
                    .clip(RoundedCornerShape(30.dp)),
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.height(8.dp)) // Add some spacing between the image and the text
            Text(
                modifier = Modifier
                    .,
                text = "Auto",
                color = Color.Black,
                fontSize = 15.sp
            )
        }
    }*/

    /*Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .clickable(onClick = onCarSelected)
            .padding(vertical = 16.dp)
        ,
    ) {
        Image(
            modifier = Modifier
                .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(30.dp)),
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = car.name,
            fontSize = 15.sp
        )
    }*/

/*suspend fun getAllCarIds(): List<String> {
    val firestore = FirebaseFirestore.getInstance()
    val carsCollection = firestore.collection("cars")

    return try {
        val querySnapshot = carsCollection.get().await()
        val carIds = mutableListOf<String>()

        for (document in querySnapshot.documents) {
            carIds.add(document.id)
        }
        carIds
    } catch (e: Exception) {
        // Handle any errors here
        emptyList() // Return an empty list if there's an error
    }
}*/