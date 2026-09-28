package com.example.data.local

import com.example.data.model.FoodId
import com.example.data.model.FoodTrend
import com.example.data.model.PlayerProfileEntity
import com.example.data.model.UpgradeId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val gameDao: GameDao) {

  val profileFlow: Flow<PlayerProfileEntity> = gameDao.getPlayerProfile().map { entity ->
    entity ?: PlayerProfileEntity().also { defaultEntity ->
      gameDao.insertOrUpdateProfile(defaultEntity)
    }
  }

  suspend fun getProfile(): PlayerProfileEntity {
    var p = gameDao.getPlayerProfileDirect()
    if (p == null) {
      p = PlayerProfileEntity()
      gameDao.insertOrUpdateProfile(p)
    }
    return p
  }

  suspend fun saveProfile(profile: PlayerProfileEntity) {
    gameDao.insertOrUpdateProfile(profile)
  }

  suspend fun addCash(amount: Int) {
    val current = getProfile()
    saveProfile(current.copy(cash = (current.cash + amount).coerceAtLeast(0)))
  }

  suspend fun addXpAndCheckLevelUp(xpEarned: Int): Pair<Int, Boolean> {
    val current = getProfile()
    var newXp = current.xp + xpEarned
    var newLevel = current.level
    var leveledUp = false

    // XP thresholds: Level 1: 0, Level 2: 150, Level 3: 400, Level 4: 800, Level 5: 1500
    val xpThresholds = listOf(0, 150, 400, 800, 1500, 3000)
    while (newLevel < xpThresholds.size && newXp >= xpThresholds[newLevel]) {
      newLevel++
      leveledUp = true
    }

    saveProfile(current.copy(xp = newXp, level = newLevel))
    return Pair(newLevel, leveledUp)
  }

  suspend fun unlockFood(foodId: FoodId) {
    val current = getProfile()
    val unlocked = current.unlockedFoodsCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    unlocked.add(foodId.name)
    saveProfile(current.copy(unlockedFoodsCsv = unlocked.joinToString(",")))
  }

  suspend fun buyUpgrade(upgradeId: UpgradeId): Boolean {
    val current = getProfile()
    if (current.cash < upgradeId.cost) return false

    val owned = current.ownedUpgradesCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
    if (owned.contains(upgradeId.name)) return false

    owned.add(upgradeId.name)
    saveProfile(
      current.copy(
        cash = current.cash - upgradeId.cost,
        ownedUpgradesCsv = owned.joinToString(",")
      )
    )
    return true
  }

  suspend fun advanceDayAndRollTrend(): FoodTrend {
    val current = getProfile()
    val nextDay = current.day + 1
    var daysLeft = current.trendDaysLeft - 1
    var trendFoodName = current.currentTrendFood

    if (daysLeft <= 0) {
      // Pick next trend from available exciting foods
      val trendingCandidates = listOf(
        FoodId.LAP_XUONG_NUONG_DA,
        FoodId.KHOAI_LOC_XOAY,
        FoodId.NEM_CHUA_RAN,
        FoodId.CA_VIEN_CHIEN
      )
      val nextTrend = trendingCandidates.random()
      trendFoodName = nextTrend.name
      daysLeft = 3 // 3 in-game days per trend
    }

    val updatedProfile = current.copy(
      day = nextDay,
      currentTrendFood = trendFoodName,
      trendDaysLeft = daysLeft
    )
    saveProfile(updatedProfile)

    val foodEnum = try {
      FoodId.valueOf(trendFoodName)
    } catch (_: Exception) {
      FoodId.LAP_XUONG_NUONG_DA
    }
    return FoodTrend(foodId = foodEnum, remainingDays = daysLeft)
  }

  suspend fun resetGameData() {
    val defaultProfile = PlayerProfileEntity()
    gameDao.insertOrUpdateProfile(defaultProfile)
  }
}
