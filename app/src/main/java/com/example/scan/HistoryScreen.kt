package com.example.scan

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.scan.database.ScanDatabase
import com.example.scan.module.ScanEntry
import java.util.Date
import java.util.Map.entry

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scanDao = remember { ScanDatabase.getDatabase(context).scanDao() }
    val scope = rememberCoroutineScope()
    var entries by remember { mutableStateOf<List<ScanEntry>>(emptyList()) }

    // Загружаем из БД при запуске
    LaunchedEffect(Unit) {
        entries = scanDao.getAll()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Scan History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(entries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Если это скан документа
                        entry.uri?.let {
                            Image(
                                painter = rememberAsyncImagePainter(model = Uri.parse(it)),
                                contentDescription = "Scanned Document",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                        // Если это штрих-код
                        entry.barcodeValue?.let {
                            Text(
                                text = "Barcode: $it",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Дата
                        Text(
                            text = "Scanned at: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(entry.timestamp))}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
