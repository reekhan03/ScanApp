package com.example.scan

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.scan.ui.theme.ScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanTheme {
                val scannedImages = remember { mutableStateListOf<Uri>() }
                val navController = rememberNavController()
                NavGraph(navController, scannedImages)
            }
        }
    }
}
