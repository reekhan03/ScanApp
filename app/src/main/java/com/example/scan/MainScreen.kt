package com.example.scan


import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    scannedImages: SnapshotStateList<Uri>
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val modes = listOf(
        stringResource(R.string.full),
        stringResource(R.string.base),
        stringResource(R.string.base_with_filter)
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(modes[0]) }
    var enableGalleryImport by remember { mutableStateOf(true) }
    var pageLimit by remember { mutableIntStateOf(2) }
    var scannerMode by remember { mutableIntStateOf(GmsDocumentScannerOptions.SCANNER_MODE_FULL) }

    // Запуск сканера
    val scannerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val gmsResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            gmsResult?.pages?.forEach { page ->
                val imageUri = page.imageUri
                val savedUri = saveImageToGallery(context, imageUri)
                savedUri?.let { scannedImages.add(it) }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(136.dp))
            // Опции для выбора режима сканера
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.scanner_feature_mode))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedOptionText,
                        onValueChange = { selectedOptionText = it },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                    )

                    val filteringOptions =
                        modes.filterNot { it.equals(selectedOptionText, ignoreCase = true) }
                    if (filteringOptions.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filteringOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedOptionText = selectionOption
                                        expanded = false
                                        scannerMode = when (selectionOption) {
                                            context.getString(R.string.full) -> GmsDocumentScannerOptions.SCANNER_MODE_FULL
                                            context.getString(R.string.base) -> GmsDocumentScannerOptions.SCANNER_MODE_BASE
                                            context.getString(R.string.base_with_filter) -> GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER
                                            else -> GmsDocumentScannerOptions.SCANNER_MODE_FULL
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Установка лимита на количество страниц
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.set_page_limit_per_scan))
                OutlinedTextField(
                    value = "$pageLimit",
                    onValueChange = {
                        if (it.isNotEmpty() && it.isDigitsOnly()) {
                            pageLimit = it.toInt()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            // Включение импорта из галереи
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.enable_gallery_import))
                Checkbox(
                    checked = enableGalleryImport,
                    onCheckedChange = { enableGalleryImport = it }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Кнопка для запуска сканирования
                    Button(onClick = {
                        val scannerOptions = GmsDocumentScannerOptions.Builder()
                            .setGalleryImportAllowed(enableGalleryImport)
                            .setPageLimit(pageLimit)
                            .setResultFormats(
                                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
                            )
                            .setScannerMode(scannerMode)
                            .build()
                        val scanner = GmsDocumentScanning.getClient(scannerOptions)
                        scanner.getStartScanIntent(context as Activity)
                            .addOnSuccessListener { intentSender ->
                                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                            }
                            .addOnFailureListener {
                                scope.launch {
                                    snackbarHostState.showSnackbar(it.localizedMessage ?: "Smth went wrong")
                                }
                            }
                    }) {
                        Text(stringResource(R.string.scan))
                    }

                    Spacer(modifier = Modifier
                        .width(16.dp))

                    Button(onClick = {
                        // Переход на экран "history"
                        navController.navigate("history")
                    }) {
                        Text("Scanned Documents")
                    }
                }


                Button(onClick = {
                    navController.navigate("barcode")
                }) {
                    Text("Scan Barcode")
                }
            }
        }
    }
}
