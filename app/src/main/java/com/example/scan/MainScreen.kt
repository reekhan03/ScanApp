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
import androidx.compose.ui.res.colorResource

@Composable
fun MainScreen(
    navController: NavHostController,
    buttons: List<String>
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
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
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(label)
                    }
                }
            }
        }
}

//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.Center,
//        ) {
//            // Кнопка для перехода на экран сканирования
//            Button(
//                onClick = {
//                    navController.navigate("scan")
//                } ) {
//                Text("Scan Documents")
//            }
//
//            Spacer(modifier = Modifier
//                .width(16.dp))
//
//            Button(onClick = {
//                // Переход на экран "history"
//                navController.navigate("history")
//            }) {
//                Text("History")
//            }
//        }
//
//
//        Button(onClick = {
//            navController.navigate("barcode")
//        }) {
//            Text("Scan Barcode")
//        }
//    }


