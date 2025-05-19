package com.example.scan.module


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_entries")
data class ScanEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String?,
    val barcodeValue: String?,
    val timestamp: Long = System.currentTimeMillis()
)