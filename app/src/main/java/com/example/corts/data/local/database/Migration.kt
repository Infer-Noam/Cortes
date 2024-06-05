package com.example.corts.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Drop the old table
        db.execSQL("DROP TABLE IF EXISTS `Point`")
        // Create the new table
        db.execSQL("CREATE TABLE IF NOT EXISTS `Point` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `longitude` DOUBLE NOT NULL, `latitude ` DOUBLE NOT NULL, `date` STRING NOT NULL)")
    }
}

