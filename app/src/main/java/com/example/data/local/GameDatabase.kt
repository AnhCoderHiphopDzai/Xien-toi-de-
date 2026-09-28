package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.PlayerProfileEntity

@Database(entities = [PlayerProfileEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
  abstract fun gameDao(): GameDao

  companion object {
    @Volatile
    private var INSTANCE: GameDatabase? = null

    fun getDatabase(context: Context): GameDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          GameDatabase::class.java,
          "xien_toi_de_database.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
