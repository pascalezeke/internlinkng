package com.internlinkng.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [HospitalEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hospitalDao(): HospitalDao
    
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE hospitals ADD COLUMN created TEXT NOT NULL DEFAULT '2024-07-28'")
            }
        }
    }
} 