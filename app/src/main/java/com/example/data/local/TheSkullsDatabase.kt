package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookmarkEntity::class,
        CommunityReviewEntity::class,
        CommunityNewsEntity::class,
        UserSubmittedReviewEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class TheSkullsDatabase : RoomDatabase() {
    abstract fun theSkullsDao(): TheSkullsDao

    companion object {
        @Volatile
        private var INSTANCE: TheSkullsDatabase? = null

        fun getDatabase(context: Context): TheSkullsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TheSkullsDatabase::class.java,
                    "the_skulls_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
