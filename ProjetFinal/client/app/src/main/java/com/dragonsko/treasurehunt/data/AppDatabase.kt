package com.dragonsko.treasurehunt.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import kotlinx.coroutines.CoroutineScope


// DROP DB Util:
// applicationContext.deleteDatabase("app_database")

@Database(
    version = 1,
    entities = [Quest::class, Treasure::class],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun QuestDao(): QuestDao
    abstract fun TreasuresDao(): TreasuresDao

    // From https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, coroutineScope: CoroutineScope): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

