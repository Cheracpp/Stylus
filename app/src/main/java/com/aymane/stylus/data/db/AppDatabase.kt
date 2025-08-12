package com.aymane.stylus.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aymane.stylus.data.db.dao.DraftDao
import com.aymane.stylus.data.db.entity.DraftEntity
import com.aymane.stylus.util.AppConstants

/**
 * Room database for the Stylus application.
 * Manages local storage of drafts and other application data.
 */
@Database(entities = [DraftEntity::class], version = AppConstants.Database.DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun draftDao(): DraftDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AppConstants.Database.DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}