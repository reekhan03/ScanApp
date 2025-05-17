package com.example.scan

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.scan.MainScreen
import com.example.scan.HistoryScreen

@Composable
fun NavGraph(navController: NavHostController, scannedImages: SnapshotStateList<Uri>) {
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController, listOf("Scan Documents", "History", "Scan Barcode"))
        }
        composable("history") {
            HistoryScreen(scannedImages, navController)
        }
        composable("barcode") { BarcodeScannerScreen(navController)
        }
        composable("scan"){
            ScanScreen(navController,scannedImages)
        }


    }
}
