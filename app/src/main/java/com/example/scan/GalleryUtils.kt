package com.example.scan

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

fun saveImageToGallery(context: Context, sourceUri: Uri): Uri? {
    val contentResolver = context.contentResolver
    val fileName = "scanned_${System.currentTimeMillis()}.jpg"
    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ScannedDocuments")
    }

    return try {
        val uri = contentResolver.insert(imageCollection, contentValues)
        uri?.let { destUri ->
            contentResolver.openInputStream(sourceUri)?.use { input ->
                contentResolver.openOutputStream(destUri)?.use { output ->
                    input.copyTo(output)
                }
            }
        }
        uri
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
