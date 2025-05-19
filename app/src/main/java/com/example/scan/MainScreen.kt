package com.example.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    navController: NavHostController,
    buttons: List<String>
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(buttons) { label ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                ) {
                    Button(
                        onClick = {
                            when (label) {
                                "Scan Documents" -> navController.navigate("scan")
                                "History" -> navController.navigate("history")
                                "Scan Barcode" -> navController.navigate("barcode")
                                "Settings" -> navController.navigate("settings")
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (label) {
                                "Scan Documents" -> colorResource(id = R.color.pastel_green)
                                "History" -> colorResource(id = R.color.pastel_yellow)
                                "Scan Barcode" -> colorResource(id = R.color.pastel_blue)
                                "Settings" ->colorResource(id = R.color.pastel_pink)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            contentColor = Color.Black
                        )
                    ) {
                        Text(label)
                    }
                }
            }
        }
    }

}

