package com.ruki.tierbnb.screens

import android.Manifest
import com.ruki.tierbnb.models.Car
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ruki.tierbnb.R
import com.ruki.tierbnb.components.NotificationHandler
import com.ruki.tierbnb.components.showToast
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.ui.theme.GrayBackground
import com.ruki.tierbnb.ui.theme.LightBlue
import com.ruki.tierbnb.view_models.CarViewModel
import com.ruki.tierbnb.view_models.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@RequiresApi(TIRAMISU)
@Composable
fun CarReservationScreen(
    carId: String,
    navController: NavController,
    carViewModel: CarViewModel,
    auth: FirebaseAuth,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current

    val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val notificationHandler = NotificationHandler(context)

    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    val userAuth = auth.currentUser

    var car by remember { mutableStateOf<Car?>(null) }

    val cascoPrice = 300

    var checkedCasco by remember { mutableStateOf(false) }
    var olderThen by remember { mutableStateOf(false) }

    var firstDateSelected by remember { mutableStateOf("") }
    var lastDateSelected by remember { mutableStateOf("") }
    var fullPrice by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(userAuth?.phoneNumber ?: "") }

    var expanded by remember { mutableStateOf(false) }

    val countries = Locale.getISOCountries().map { code ->
        Locale("", code).displayCountry
    }.sorted()
    var selectedCountry by remember { mutableStateOf(countries[0]) }

    val cars by carViewModel.cars.collectAsState()
    val user by userViewModel.user.collectAsState()

    car = cars.find { it.id == carId }

    car?.let {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.navigate(NavigationItem.CarDetails.createRoute(it.id)) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }

                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "ZAHTIJEV ZA REZERVACIJU",
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 25.dp)
                        .width(170.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 8.dp)
                    .border(
                        width = 1.dp,
                        color = GrayBackground,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .background(GrayBackground),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
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
                                        .height(180.dp)
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
                                text = "${it.type} ${it.name} | ${it.year}",
                                fontSize = 16.sp,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${it.city} | ${it.dealership}",
                                fontSize = 13.sp,
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            DatePicker(
                                label = "Početak",
                                value = firstDateSelected,
                                onValueChange = { newValue ->
                                    firstDateSelected = newValue
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DatePicker(
                                label = "Kraj",
                                value = lastDateSelected,
                                onValueChange = { newValue ->
                                    lastDateSelected = newValue
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Ime", color = Color.Black) },
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Start),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LightBlue,
                                    unfocusedBorderColor = Color.Black,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = surname,
                                onValueChange = { surname = it },
                                label = { Text(text = "Prezime", color = Color.Black) },
                                modifier = Modifier
                                    .padding(vertical = 2.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Start),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LightBlue,
                                    unfocusedBorderColor = Color.Black,
                                )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = {
                                            expanded = !expanded
                                        }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedCountry,
                                            onValueChange = { selectedCountry = it },
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expanded
                                                )
                                            },
                                            label = { Text("Zemlja", color = Color.Black) },
                                            modifier = Modifier
                                                .padding(vertical = 2.dp)
                                                .menuAnchor(),
                                            shape = RoundedCornerShape(10.dp),
                                            textStyle = TextStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Start
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = LightBlue,
                                                unfocusedBorderColor = Color.Black,
                                            )
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            countries.forEach { item ->
                                                DropdownMenuItem(
                                                    text = { Text(text = item) },
                                                    onClick = {
                                                        selectedCountry = item
                                                        expanded = false
                                                        showToast(context, item)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text("Phone Number", color = Color.Black) },
                                    modifier = Modifier.weight(1.3f),
                                    shape = RoundedCornerShape(10.dp),
                                    textStyle = TextStyle(
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Start
                                    ),
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LightBlue,
                                        unfocusedBorderColor = Color.Black,)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    colors = CheckboxDefaults.colors(LightBlue),
                                    checked = checkedCasco,
                                    onCheckedChange = { checkedCasco = it }
                                )
                                Text("KASKO Osig.")

                                Checkbox(
                                    colors = CheckboxDefaults.colors(LightBlue),
                                    checked = olderThen,
                                    onCheckedChange = { olderThen = it }
                                )
                                Text("Stariji od ${it.minAge}.")
                            }

                            HorizontalDivider(
                                color = Color.LightGray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            fullPrice = priceCalculator(firstDateSelected, lastDateSelected, it.dailyPrice, checkedCasco, cascoPrice)

                            Text(
                                text = "Ukupna cijena: $fullPrice",
                                modifier = Modifier
                                    .padding(start = 8.dp, bottom = 5.dp)
                                    .align(Alignment.CenterHorizontally),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            if(user?.reservedCar?.carId != "") {
                                Text(
                                    color = Color.Red,
                                    modifier = Modifier
                                        .padding(start = 4.dp, bottom = 2.dp)
                                        .align(Alignment.CenterHorizontally),
                                    text = "Već ste rezervirali auto"
                                )
                            }


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = {
                                            updateReservation(it, auth, firstDateSelected, lastDateSelected, true, carViewModel) { success ->
                                                if (success) {
                                                    notificationHandler.showSimpleNotification(it, firstDateSelected, lastDateSelected, fullPrice)
                                                    println(carViewModel.cars)

                                                    navController.navigate(NavigationItem.HomeScreen.route) {
                                                        popUpTo(navController.graph.startDestinationId)
                                                    }
                                                } else {
                                                    showToast(context,"Ne uspjela rezervacija")
                                                }
                                            }
                                        },
                                        enabled = olderThen
                                                && name.isNotEmpty()
                                                && surname.isNotEmpty()
                                                && phoneNumber.isNotEmpty()
                                                && selectedCountry.isNotEmpty()
                                                && firstDateSelected.isNotEmpty()
                                                && lastDateSelected.isNotEmpty()
                                                && user?.reservedCar?.carId == "",
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .width(300.dp)
                                            .height(50.dp),
                                        shape = RoundedCornerShape(15.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LightBlue,
                                            contentColor = Color.White,
                                            disabledContainerColor = Color.Gray,
                                            disabledContentColor = Color.White)
                                    ) {
                                        Text(
                                            color = Color.Black,
                                            text = "Zatraži rezervaciju")
                                    }
                                }
                            }
                        }
                    }
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

fun updateReservation(
    it: Car,
    auth: FirebaseAuth,
    firstDateSelected: String,
    lastDateSelected: String,
    reserved: Boolean,
    carViewModel: CarViewModel,
    callback: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val userAuth = auth.currentUser

    val carRef = db.collection("cars").document(it.id)
    val userRef = userAuth?.let {
        it1 -> db.collection("users").document(it1.uid)
    }

    if (userRef == null) {
        callback(false)
        return
    }

    val batch = db.batch()
    batch.update(carRef, "reserved", reserved)
    batch.update(userRef, "reservedCar", mapOf(
        "carId" to it.id,
        "firstDate" to firstDateSelected,
        "lastDate" to lastDateSelected
    ))

    batch.commit()
        .addOnSuccessListener {
            Log.d(TAG, "Batch update successful!")
            carViewModel.fetchCars()
            callback(true)
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Batch update failed", e)
            callback(false)
        }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    pattern: String = "yyyy-MM-dd",
) {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val date = if (value.isNotBlank()) LocalDate.parse(value, formatter) else LocalDate.now()
    val today = LocalDate.now()
    val context = LocalContext.current

    val dialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            if (!selectedDate.isBefore(today)) {
                onValueChange(selectedDate.format(formatter))
            } else {
                showToast(context, "Cannot select a date before today")
            }
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth,
    ).apply {
        datePicker.minDate = today.toEpochDay() * 24 * 60 * 60 * 1000
    }

    TextField(
        label = { Text(label, color = Color.White) },
        value = value,
        onValueChange = onValueChange,
        enabled = false,
        modifier = Modifier
            .clickable { dialog.show() },
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Black,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun priceCalculator(
    firstDateSelected: String,
    lastDateSelected: String,
    dailyPrice: String,
    checkedCasco: Boolean,
    cascoPrice: Int
): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val daysBetween = runCatching {
        val firstDate = LocalDate.parse(firstDateSelected, formatter)
        val lastDate = LocalDate.parse(lastDateSelected, formatter)
        ChronoUnit.DAYS.between(firstDate, lastDate).toInt() + 1
    }.getOrDefault(0)

    val totalPrice = (daysBetween * dailyPrice.toIntOrNull()!!) + (if (checkedCasco) cascoPrice else 0)


    return totalPrice.toString()
}