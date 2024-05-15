package com.ruki.tierbnb.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(onSearch = { query ->
            // Handle search query
            // searchViewModel.performSearch(query)
        })
        SliderNavigationBar(
            selectedOption = selectedOption,
            onOptionSelected = { newOption ->
                selectedOption = newOption
            }
        )

        Divider(modifier = Modifier
            .padding(bottom = 5.dp),
            color = Color.LightGray,
            thickness = 2.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(12.dp))
            .background(color = Color.White)
            .padding(horizontal = 8.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = "Search Icon",
            tint = Color.Black,
            modifier = Modifier.padding(start = 8.dp, end = 4.dp)
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
            modifier = Modifier.weight(1f)
        )
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
            .padding(bottom = 2.dp, top = 15.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .bottomBorder(if (isSelected) Color.Black else Color.Transparent, yOffSet = -10)
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