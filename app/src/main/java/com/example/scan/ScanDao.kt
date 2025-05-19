package com.example.scan

import androidx.room.*
import com.example.scan.module.ScanEntry

@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ScanEntry)

    @Query("SELECT * FROM scan_entries ORDER BY timestamp DESC")
    suspend fun getAll(): List<ScanEntry>

    @Query("DELETE FROM scan_entries")
    suspend fun deleteAll()
}