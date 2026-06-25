package com.example.musicunlocked

import android.content.Context
import androidx.room.Room
import com.example.musicunlocked.database.AppDatabase

object DatabaseProvider {

    private var db: AppDatabase? = null

    fun getDb(context: Context): AppDatabase {

        if (db == null) {

            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "music_db"
            )
                .fallbackToDestructiveMigration(false)
                .build()
        }

        return db!!
    }
}