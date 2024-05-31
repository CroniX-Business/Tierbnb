package com.ruki.tierbnb.screens

import com.ruki.tierbnb.models.Car
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
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
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.android.gms.location.FusedLocationProviderClient
import com.ruki.tierbnb.costume_modifier.bottomBorder
import com.ruki.tierbnb.models.CarItems
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.ui.theme.LightBlue
import com.ruki.tierbnb.view_models.CarViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun MainScreen(
    navController: NavController,
    fusedLocationClient: FusedLocationProviderClient,
    carViewModel: CarViewModel
) {
    var selectedOption by remember { mutableStateOf("Near") }
    var cityName by remember { mutableStateOf("Unknown") }
    var userLatitude by remember { mutableDoubleStateOf(0.0) }
    var userLongitude by remember { mutableDoubleStateOf(0.0) }
    var searchQuery by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    var showDialog by remember { mutableStateOf(false) }

    val maxDistanceInKm = 50.0

    var distanceValue by remember { mutableDoubleStateOf(maxDistanceInKm) }
    var newDistanceValue by remember { mutableDoubleStateOf(maxDistanceInKm) }

    val cars by carViewModel.cars.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight - 55.dp)
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
                    searchQuery = query
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

        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Gray
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
                            val address = addresses[0]
                            cityName = address.locality

                        }
                    }
                }
            }
            .addOnFailureListener {
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
                        .width(180.dp)
                        .wrapContentHeight()
                        .border(
                            width = 2.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(20)
                        )
                        .clip(RoundedCornerShape(20))
                        .background(Color.LightGray),
                    text = "$cityName +${BigDecimal(distanceValue).setScale(1, RoundingMode.HALF_UP)}km",
                    fontSize = 13.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = { showDialog = true },
                ) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Filter")
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        distanceValue = newDistanceValue
                    },
                    title = { Text(text = "Change Distance") },
                    text = {
                        Slider(
                            value = newDistanceValue.toFloat(),
                            onValueChange = { value ->
                                newDistanceValue = value.toDouble()
                            },
                            valueRange = 1f..500f,
                            steps = 10,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Text(text = BigDecimal(newDistanceValue).setScale(1, RoundingMode.HALF_UP).toString())
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                distanceValue = newDistanceValue
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                                newDistanceValue = distanceValue
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        val filteredCars = cars.filter { car ->
            when (selectedOption) {
                "All" -> {
                    searchQuery.isEmpty() || (car.type.contains(searchQuery, ignoreCase = true) || car.name.contains(searchQuery, ignoreCase = true))
                }
                "Near" -> {
                    val carLatitude = car.latitude
                    val carLongitude = car.longitude
                    val distance = calculateDistanceInKm(userLatitude, userLongitude, carLatitude, carLongitude)
                    distance <= distanceValue
                }
                "Luxury" -> car.luxury
                else -> car.type == selectedOption
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(filteredCars) { _, car ->
                CarItem(
                    car = car,
                    onCarSelected = {
                        navController.navigate(NavigationItem.CarDetails.createRoute(car.id))
                    }
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
    val getOptions = CarItems::class.sealedSubclasses.mapNotNull { subclass ->
        subclass.objectInstance
    }
    val options = getOptions.sortedBy { it.order }

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
            itemsIndexed(options) { _, option ->
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

/*@Composable
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
}*/

@Composable
fun CarItem(
    car: Car,
    onCarSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (car.reserved) 350.dp else 320.dp)
            .padding(top = 10.dp)
            .clickable(onClick = onCarSelected)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            SubcomposeAsyncImage(
                model = car.transformedImages[0],
                contentDescription = null,
                colorFilter = if (car.reserved) {
                    ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                } else {
                    null
                },
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
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(80.dp, 80.dp),
                            color = LightBlue
                        )
                    }
                },
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
                    .padding(
                        bottom = 5.dp,
                    )
            ) {
                Text(
                    text = "${car.type} ${car.name} | ${car.year}",
                    textAlign = TextAlign.Start,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = buildString {
                        val dailyPriceDouble = car.dailyPrice.toIntOrNull() ?: 0
                        val monthlyPrice = dailyPriceDouble * 30
                        append("Day - ${car.dailyPrice}€ | Month - ${monthlyPrice}€")
                    },
                    textAlign = TextAlign.Start,
                    color = Color.Gray,
                    fontSize = 18.sp,
                )
                if(car.reserved) {
                    Text(
                        text = "REZERVIRANO",
                        textAlign = TextAlign.Start,
                        color = Color.Red,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}