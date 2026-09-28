package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
  @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
  fun getPlayerProfile(): Flow<PlayerProfileEntity?>

  @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
  suspend fun getPlayerProfileDirect(): PlayerProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

  @Update
  suspend fun updateProfile(profile: PlayerProfileEntity)
}
