package com.ruki.tierbnb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.ruki.tierbnb.costume_modifier.bottomBorder
import com.ruki.tierbnb.models.CarItems
import com.ruki.tierbnb.ui.theme.LightBlue

@Composable
fun MainScreen(navController: NavController, auth: FirebaseAuth) {
    var selectedOption by remember { mutableStateOf("Near") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        // Top Section: SearchBar and SliderNavigationBar inside a blue Box
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
                    // Handle search query
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
                .background(color = Color.LightGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "This is the bottom section",
                color = Color.White
            )
        }
    }
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
            .bottomBorder(if (isSelected) Color.Black else Color.Transparent, yOffSet = -50, width = 2.dp)
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