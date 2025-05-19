package com.example.scan

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.example.scan.database.ScanDatabase
import com.example.scan.module.ScanEntry
import com.example.scan.ui.theme.ScanTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScannerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val db = ScanDatabase.getDatabase(context)
    val dao = db.scanDao()

    // Камера: создаём Uri перед запуском
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri? {
        val imageFile = File.createTempFile(
            "barcode_image",
            ".jpg",
            context.cacheDir
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    // Камера: сделать фото
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            imageUri = cameraImageUri
        }
    }

    // Галерея: выбрать фото
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Обработка изображения
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            try {
                val image = InputImage.fromFilePath(context, uri)
                val scanner = BarcodeScanning.getClient()

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            val value = barcodes[0].rawValue
                            value?.let {
                                scope.launch {
                                    dao.insert(ScanEntry(uri  =null, barcodeValue = it))
                                    println("✅ Вставлен штрих-код в базу: $it")
                                }
                                val intent = if (it.startsWith("http")) {
                                    Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                } else {
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(it)}"))
                                }
                                context.startActivity(intent)
                            }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Штрих-код не найден.") }
                        }
                    }
                    .addOnFailureListener {
                        scope.launch { snackbarHostState.showSnackbar("Ошибка при сканировании.") }
                    }
            } catch (e: IOException) {
                scope.launch { snackbarHostState.showSnackbar("Не удалось загрузить изображение.") }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BarCode Scanner") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier
                .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    val uri = createImageUri()
                    cameraImageUri = uri
                    uri?.let { takePictureLauncher.launch(it) }
                }, modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.button_color),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    ))
                {
                    Text("Take picture")
                }

                Button(onClick = {
                    pickImageLauncher.launch("image/*")
                }, modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.button_color),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )) {
                    Text("Open gallery")
                }
            }

        }
    }
}
@Preview(showBackground = true)
@Composable
fun BarcodeScannerScreenPreview() {
    ScanTheme {
        // Используем фейковый NavController для превью
        BarcodeScannerScreen(navController = rememberNavController())
    }
}

