package com.ruki.tierbnb.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.ruki.tierbnb.models.Car
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.ruki.tierbnb.R
import com.ruki.tierbnb.models.NavigationItem
import com.ruki.tierbnb.ui.theme.GrayBackground
import com.ruki.tierbnb.ui.theme.LightBlue
import com.ruki.tierbnb.view_models.CarViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarDetailsScreen(carId: String, navController: NavController, carViewModel: CarViewModel) {
    var car by remember { mutableStateOf<Car?>(null) }

    val cars by carViewModel.cars.collectAsState()

    car = cars.find { it.id == carId }

    car?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val pagerState = rememberPagerState(initialPage = 0, pageCount = { car!!.transformedImages.size })

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .height(350.dp)
                        .fillMaxWidth()
                ) { page ->
                    Card(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        SubcomposeAsyncImage(
                            model = it.transformedImages[page],
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
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
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                ) {
                    IconButton(
                        onClick = { navController.navigate(NavigationItem.HomeScreen.route) }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
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
                            .padding(bottom = 8.dp)
                    ) {
                        Text(text = "${it.type} ${it.name}", modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(text = "${it.city} | ${it.dealership}", modifier = Modifier.weight(1f))
                    }

                    HorizontalDivider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
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
                                Image(
                                    painter = painterResource(id = R.drawable.year),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = it.year)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.horsepower),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = it.horsePower)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gearshift),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = it.gear)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.maxseats),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = it.maxSeat)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.minage),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Min. dob: ${it.minAge}")
                            }

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
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text("Dan - ${it.dailyPrice}€")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = buildString {
                                val dailyPriceDouble = it.dailyPrice.toIntOrNull() ?: 0
                                val monthlyPrice = dailyPriceDouble * 30
                                append("Month - ${monthlyPrice}€")
                            },)
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate(NavigationItem.CarReservation.createRoute(it.id))
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .width(300.dp)
                                    .height(50.dp),
                                shape = RoundedCornerShape(15.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LightBlue, contentColor = Color.White)
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