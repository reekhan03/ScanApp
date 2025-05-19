package com.example.scan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scan.ScanDao
import com.example.scan.module.ScanEntry

@Database(entities = [ScanEntry::class], version = 1)
abstract class ScanDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao

    companion object{
        @Volatile private var INSTANCE: ScanDatabase? = null

        fun getDatabase(context: Context): ScanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScanDatabase::class.java,
                    "scan_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}