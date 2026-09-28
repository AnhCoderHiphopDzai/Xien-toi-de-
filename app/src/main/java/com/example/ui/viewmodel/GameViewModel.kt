package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.local.GameDatabase
import com.example.data.local.GameRepository
import com.example.data.model.CookingSlot
import com.example.data.model.CookingStationType
import com.example.data.model.CookingStatus
import com.example.data.model.CustomerOrder
import com.example.data.model.CustomerType
import com.example.data.model.FoodId
import com.example.data.model.FoodTrend
import com.example.data.model.LocationId
import com.example.data.model.PlatedItem
import com.example.data.model.PlayerProfileEntity
import com.example.data.model.SauceType
import com.example.data.model.ShiftReport
import com.example.data.model.ShiftSetup
import com.example.data.model.SpeakerLevel
import com.example.data.model.UpgradeId
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class AppScreen {
  MENU,
  SETUP,
  GAMEPLAY,
  POLICE_RAID,
  GETAWAY_MINIGAME,
  SUMMARY,
  SHOP
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: GameRepository
  val soundManager: SoundManager = SoundManager(application)

  private val _currentScreen = MutableStateFlow(AppScreen.MENU)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _playerProfile = MutableStateFlow(PlayerProfileEntity())
  val playerProfile: StateFlow<PlayerProfileEntity> = _playerProfile.asStateFlow()

  private val _trendingFood = MutableStateFlow<FoodTrend?>(null)
  val trendingFood: StateFlow<FoodTrend?> = _trendingFood.asStateFlow()

  // Current active shift state
  private val _shiftSetup = MutableStateFlow(ShiftSetup())
  val shiftSetup: StateFlow<ShiftSetup> = _shiftSetup.asStateFlow()

  private val _clockTimeText = MutableStateFlow("16:00")
  val clockTimeText: StateFlow<String> = _clockTimeText.asStateFlow()

  private val _currentCash = MutableStateFlow(120_000)
  val currentCash: StateFlow<Int> = _currentCash.asStateFlow()

  private val _riskPercent = MutableStateFlow(0f)
  val riskPercent: StateFlow<Float> = _riskPercent.asStateFlow()

  private val _tableRisk = MutableStateFlow(0f)
  val tableRisk: StateFlow<Float> = _tableRisk.asStateFlow()

  private val _speakerRisk = MutableStateFlow(0f)
  val speakerRisk: StateFlow<Float> = _speakerRisk.asStateFlow()

  private val _trashRisk = MutableStateFlow(0f)
  val trashRisk: StateFlow<Float> = _trashRisk.asStateFlow()

  private val _shipperRisk = MutableStateFlow(0f)
  val shipperRisk: StateFlow<Float> = _shipperRisk.asStateFlow()

  private val _trashAmount = MutableStateFlow(0f)
  val trashAmount: StateFlow<Float> = _trashAmount.asStateFlow()

  private val _isSpeakerMuted = MutableStateFlow(false)
  val isSpeakerMuted: StateFlow<Boolean> = _isSpeakerMuted.asStateFlow()

  private val _cookingSlots = MutableStateFlow<List<CookingSlot>>(emptyList())
  val cookingSlots: StateFlow<List<CookingSlot>> = _cookingSlots.asStateFlow()

  private val _platedItems = MutableStateFlow<List<PlatedItem>>(emptyList())
  val platedItems: StateFlow<List<PlatedItem>> = _platedItems.asStateFlow()

  private val _activeOrders = MutableStateFlow<List<CustomerOrder>>(emptyList())
  val activeOrders: StateFlow<List<CustomerOrder>> = _activeOrders.asStateFlow()

  // Police Raid Emergency State
  private val _raidTimeLeftSec = MutableStateFlow(10.0f)
  val raidTimeLeftSec: StateFlow<Float> = _raidTimeLeftSec.asStateFlow()

  private val _tablesPackedPercent = MutableStateFlow(0f)
  val tablesPackedPercent: StateFlow<Float> = _tablesPackedPercent.asStateFlow()

  private val _kitchenPackedPercent = MutableStateFlow(0f)
  val kitchenPackedPercent: StateFlow<Float> = _kitchenPackedPercent.asStateFlow()

  // Shift performance accumulation
  private var shiftRevenue = 0
  private var shiftTips = 0
  private var shiftIngredientCost = 0
  private var shiftCustomersServed = 0
  private var shiftCustomersLost = 0
  private var shiftPeakRisk = 0
  private var policeFine = 0
  private var damageCost = 0
  private var wasRaidHit = false
  private var escapedPolice = false
  private var didLevelUp = false

  private val _shiftReport = MutableStateFlow<ShiftReport?>(null)
  val shiftReport: StateFlow<ShiftReport?> = _shiftReport.asStateFlow()

  private val _leveledUpNotification = MutableStateFlow(false)
  val leveledUpNotification: StateFlow<Boolean> = _leveledUpNotification.asStateFlow()

  private var gameLoopJob: Job? = null
  private var policeLoopJob: Job? = null

  init {
    val db = GameDatabase.getDatabase(application)
    repository = GameRepository(db.gameDao())

    viewModelScope.launch {
      repository.profileFlow.collect { profile ->
        _playerProfile.value = profile
        _currentCash.value = profile.cash
        val trendEnum = try {
          FoodId.valueOf(profile.currentTrendFood)
        } catch (_: Exception) {
          FoodId.LAP_XUONG_NUONG_DA
        }
        _trendingFood.value = FoodTrend(trendEnum, profile.trendDaysLeft)
      }
    }
  }

  fun setScreen(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun toggleSpeaker() {
    _isSpeakerMuted.value = !_isSpeakerMuted.value
    soundManager.playClick()
  }

  fun startShift(setup: ShiftSetup) {
    _shiftSetup.value = setup
    shiftRevenue = 0
    shiftTips = 0
    shiftIngredientCost = 0
    shiftCustomersServed = 0
    shiftCustomersLost = 0
    shiftPeakRisk = 0
    policeFine = 0
    damageCost = 0
    wasRaidHit = false
    escapedPolice = false
    didLevelUp = false

    _riskPercent.value = 5f
    _trashAmount.value = 0f
    _isSpeakerMuted.value = setup.speakerVolume == SpeakerLevel.OFF

    // Initialize 4 Cooking Slots: 2 fryers, 1 stone grill, 1 drink dispenser
    _cookingSlots.value = listOf(
      CookingSlot(slotId = 0, stationType = CookingStationType.FRYER),
      CookingSlot(slotId = 1, stationType = CookingStationType.FRYER),
      CookingSlot(slotId = 2, stationType = CookingStationType.STONE_GRILL),
      CookingSlot(slotId = 3, stationType = CookingStationType.DRINK_STATION)
    )
    _platedItems.value = emptyList()
    _activeOrders.value = emptyList()

    _currentScreen.value = AppScreen.GAMEPLAY
    soundManager.playClick()

    startGameLoop()
  }

  private fun startGameLoop() {
    gameLoopJob?.cancel()
    gameLoopJob = viewModelScope.launch {
      var inGameMinute = 16 * 60 // 16:00
      val shiftEndMinute = 22 * 60 + 30 // 22:30 (approx 90s real-time for prototype)
      var customerSpawnTimer = 0f

      while (inGameMinute < shiftEndMinute && _currentScreen.value == AppScreen.GAMEPLAY) {
        delay(250) // 4 ticks per second

        inGameMinute += 1 // 1 ingame minute per 250ms = 1 hour per 15s real-time
        val hour = inGameMinute / 60
        val minute = inGameMinute % 60
        _clockTimeText.value = "%02d:%02d".format(hour, minute)

        updateCookingSlots(deltaSec = 0.25f)
        updateOrders(deltaSec = 0.25f)

        // Customer spawn calculation
        customerSpawnTimer += 0.25f
        val maxCapacity = _shiftSetup.value.tableCount * 2 + 3
        val speakerBoost = if (_isSpeakerMuted.value) 1.0f else _shiftSetup.value.speakerVolume.crowdBoost
        val spawnInterval = (7.0f / speakerBoost).coerceAtLeast(2.8f)

        if (customerSpawnTimer >= spawnInterval && _activeOrders.value.size < maxCapacity) {
          customerSpawnTimer = 0f
          spawnNewCustomer()
        }

        // Trash accumulation
        val hasTrashUpgrade = _playerProfile.value.ownedUpgradesCsv.contains(UpgradeId.BIG_TRASH_BIN.name)
        val trashRate = if (hasTrashUpgrade) 0.05f else 0.12f
        _trashAmount.value = (_trashAmount.value + trashRate).coerceIn(0f, 100f)

        // Calculate Risk Factors
        val hasStoolUpgrade = _playerProfile.value.ownedUpgradesCsv.contains(UpgradeId.COMPACT_STOOLS.name)
        val stoolMultiplier = if (hasStoolUpgrade) 0.5f else 1.0f
        val tablesRiskVal = _shiftSetup.value.tableCount * 5f * stoolMultiplier
        val speakerRiskVal = if (_isSpeakerMuted.value) 0f else _shiftSetup.value.speakerVolume.riskBoost * 30f
        val trashRiskVal = (_trashAmount.value * 0.35f)

        val shippersWaiting = _activeOrders.value.count { it.customerType.isShipper }
        val shipperRiskVal = (shippersWaiting * 8f)

        _tableRisk.value = tablesRiskVal
        _speakerRisk.value = speakerRiskVal
        _trashRisk.value = trashRiskVal
        _shipperRisk.value = shipperRiskVal

        val totalRisk = (tablesRiskVal + speakerRiskVal + trashRiskVal + shipperRiskVal) *
          _shiftSetup.value.locationId.basePatrolRate * 0.45f

        _riskPercent.value = totalRisk.coerceIn(0f, 100f)
        if (_riskPercent.value.toInt() > shiftPeakRisk) {
          shiftPeakRisk = _riskPercent.value.toInt()
        }

        // Trigger "Công an tới!" if risk reaches 100%
        if (_riskPercent.value >= 100f) {
          triggerPoliceRaid()
          break
        }
      }

      if (_currentScreen.value == AppScreen.GAMEPLAY) {
        // Shift concluded normally by clock time
        finishShift()
      }
    }
  }

  private fun updateCookingSlots(deltaSec: Float) {
    val hasTurboFryer = _playerProfile.value.ownedUpgradesCsv.contains(UpgradeId.FRYER_TURBO.name)
    val hasColdContainer = _playerProfile.value.ownedUpgradesCsv.contains(UpgradeId.COLD_CONTAINER.name)

    val currentList = _cookingSlots.value.map { it.copy() }
    var changed = false

    currentList.forEach { slot ->
      val food = slot.foodId
      if (food != null && slot.status == CookingStatus.COOKING) {
        val speedMultiplier = when (slot.stationType) {
          CookingStationType.FRYER -> if (hasTurboFryer) 1.35f else 1.0f
          CookingStationType.DRINK_STATION -> if (hasColdContainer) 2.0f else 1.0f
          else -> 1.0f
        }
        slot.progressSec += deltaSec * speedMultiplier
        if (slot.progressSec >= slot.targetSec) {
          slot.status = CookingStatus.DONE
          changed = true
          soundManager.playDing()
        }
      } else if (food != null && slot.status == CookingStatus.DONE && slot.stationType != CookingStationType.DRINK_STATION) {
        // Burning timer
        slot.progressSec += deltaSec
        if (slot.progressSec >= food.burnTimeSec) {
          slot.status = CookingStatus.BURNT
          changed = true
          soundManager.playBurn()
        }
      }
    }

    if (changed || currentList.any { it.status == CookingStatus.COOKING }) {
      _cookingSlots.value = currentList
    }
  }

  private fun updateOrders(deltaSec: Float) {
    val list = _activeOrders.value.map { it.copy() }.toMutableList()
    val iterator = list.iterator()
    var orderLost = false

    while (iterator.hasNext()) {
      val order = iterator.next()
      order.currentPatienceSec -= deltaSec
      if (order.currentPatienceSec <= 0f) {
        // Customer ran out of patience and left
        iterator.remove()
        shiftCustomersLost++
        orderLost = true
        soundManager.playBurn()
      }
    }

    if (orderLost || list.isNotEmpty()) {
      _activeOrders.value = list
    }
  }

  private fun spawnNewCustomer() {
    val types = listOf(
      CustomerType.HOC_SINH,
      CustomerType.SINH_VIEN,
      CustomerType.DU_KHACH,
      CustomerType.VANG_LAI,
      CustomerType.SHIPPER
    )
    val chosenType = types.random()

    val unlockedFoodsList = getUnlockedFoods()
    val trend = _trendingFood.value

    // Order 1 to 2 items
    val items = mutableListOf<FoodId>()
    if (trend != null && Random.nextFloat() < 0.65f) {
      items.add(trend.foodId)
    } else {
      items.add(unlockedFoodsList.random())
    }

    if (chosenType == CustomerType.SINH_VIEN || chosenType == CustomerType.DU_KHACH) {
      if (Random.nextBoolean()) {
        items.add(unlockedFoodsList.random())
      }
    }

    val sauces = listOf(SauceType.TUONG_OT, SauceType.SOT_ME, SauceType.TUONG_CA, SauceType.NONE)
    val requiredSauce = if (items.any { it.station == CookingStationType.DRINK_STATION && items.size == 1 }) {
      SauceType.NONE
    } else {
      sauces.random()
    }

    val newOrder = CustomerOrder(
      id = UUID.randomUUID().toString(),
      customerType = chosenType,
      items = items,
      requiredSauce = requiredSauce,
      maxPatienceSec = chosenType.basePatienceSec,
      currentPatienceSec = chosenType.basePatienceSec,
      arrivesAtSec = 0f,
      tableName = if (chosenType.isShipper) null else "Bàn #${Random.nextInt(1, (_shiftSetup.value.tableCount.coerceAtLeast(1) + 1))}"
    )

    _activeOrders.value = _activeOrders.value + newOrder
  }

  fun addFoodToCook(foodId: FoodId) {
    // Check if player has enough money for ingredient cost
    if (_currentCash.value < foodId.cost) return

    val slots = _cookingSlots.value.map { it.copy() }
    val freeSlot = slots.firstOrNull { it.stationType == foodId.station && it.foodId == null }

    if (freeSlot != null) {
      freeSlot.foodId = foodId
      freeSlot.status = CookingStatus.COOKING
      freeSlot.progressSec = 0f
      freeSlot.targetSec = foodId.cookTimeSec

      shiftIngredientCost += foodId.cost
      _currentCash.value -= foodId.cost
      _cookingSlots.value = slots

      soundManager.playSizzle()
    }
  }

  fun pickupCookedFood(slotId: Int) {
    if (_platedItems.value.size >= 3) return // plate tray full

    val slots = _cookingSlots.value.map { it.copy() }
    val slot = slots.firstOrNull { it.slotId == slotId }

    if (slot != null && slot.status == CookingStatus.DONE && slot.foodId != null) {
      val food = slot.foodId!!
      slot.foodId = null
      slot.status = CookingStatus.RAW
      slot.progressSec = 0f

      _cookingSlots.value = slots
      _platedItems.value = _platedItems.value + PlatedItem(
        id = UUID.randomUUID().toString(),
        foodId = food,
        sauce = SauceType.NONE
      )
      soundManager.playClick()
    }
  }

  fun discardBurnt(slotId: Int) {
    val slots = _cookingSlots.value.map { it.copy() }
    val slot = slots.firstOrNull { it.slotId == slotId }
    if (slot != null && slot.status == CookingStatus.BURNT) {
      slot.foodId = null
      slot.status = CookingStatus.RAW
      slot.progressSec = 0f
      _cookingSlots.value = slots
      soundManager.playClick()
    }
  }

  fun selectSauceForPlated(plateIndex: Int, sauceType: SauceType) {
    val list = _platedItems.value.toMutableList()
    if (plateIndex in list.indices) {
      list[plateIndex] = list[plateIndex].copy(sauce = sauceType)
      _platedItems.value = list
      soundManager.playClick()
    }
  }

  fun deliverPlate(plateIndex: Int) {
    val platedList = _platedItems.value.toMutableList()
    if (plateIndex !in platedList.indices) return

    val plate = platedList[plateIndex]
    val orders = _activeOrders.value.toMutableList()

    // Find matching order that needs this item and matching sauce
    val targetOrderIndex = orders.indexOfFirst { order ->
      order.items.contains(plate.foodId) &&
        (order.requiredSauce == SauceType.NONE || order.requiredSauce == plate.sauce || plate.sauce != SauceType.NONE)
    }

    if (targetOrderIndex != -1) {
      val order = orders[targetOrderIndex]
      val remainingItems = order.items.toMutableList()
      remainingItems.remove(plate.foodId)

      platedList.removeAt(plateIndex)
      _platedItems.value = platedList

      // Calculate earnings
      val isTrend = _trendingFood.value?.foodId == plate.foodId
      val basePrice = if (isTrend) (plate.foodId.price * 1.5f).toInt() else plate.foodId.price
      val ticketMulti = _shiftSetup.value.locationId.avgTicketMultiplier
      val finalPrice = (basePrice * ticketMulti).toInt()

      // Tip for fast serving
      val tip = if (order.patienceFraction > 0.5f && Random.nextFloat() < order.customerType.tipChance) {
        (finalPrice * 0.2f).toInt()
      } else 0

      shiftRevenue += finalPrice
      shiftTips += tip
      _currentCash.value += (finalPrice + tip)

      if (remainingItems.isEmpty()) {
        orders.removeAt(targetOrderIndex)
        shiftCustomersServed++
      } else {
        orders[targetOrderIndex] = order.copy(items = remainingItems)
      }
      _activeOrders.value = orders

      soundManager.playCoin()
    }
  }

  fun clearTrash() {
    _trashAmount.value = 0f
    soundManager.playClick()
  }

  private fun triggerPoliceRaid() {
    wasRaidHit = true
    soundManager.playSiren()
    _raidTimeLeftSec.value = 10.0f
    _tablesPackedPercent.value = 0f
    _kitchenPackedPercent.value = 0f
    _currentScreen.value = AppScreen.POLICE_RAID

    policeLoopJob?.cancel()
    policeLoopJob = viewModelScope.launch {
      while (_raidTimeLeftSec.value > 0f && _currentScreen.value == AppScreen.POLICE_RAID) {
        delay(100)
        _raidTimeLeftSec.value -= 0.1f
      }

      if (_currentScreen.value == AppScreen.POLICE_RAID) {
        // Failed to pack in time: BUSTED!
        soundManager.playBurn()
        policeFine = 200_000
        _currentCash.value = (_currentCash.value - policeFine).coerceAtLeast(0)
        finishShift()
      }
    }
  }

  fun tapPackTables() {
    _tablesPackedPercent.value = (_tablesPackedPercent.value + 0.34f).coerceAtMost(1.0f)
    soundManager.playClick()
  }

  fun tapPackKitchen() {
    _kitchenPackedPercent.value = (_kitchenPackedPercent.value + 0.34f).coerceAtMost(1.0f)
    soundManager.playClick()
  }

  fun escapeCartMiniGame() {
    policeLoopJob?.cancel()
    soundManager.playClick()
    _currentScreen.value = AppScreen.GETAWAY_MINIGAME
  }

  fun finishGetawayMiniGame(escapedSuccess: Boolean, damage: Int, bonusCoins: Int) {
    escapedPolice = escapedSuccess
    damageCost = damage
    _currentCash.value = (_currentCash.value - damageCost + bonusCoins).coerceAtLeast(0)
    finishShift()
  }

  fun endShiftManually() {
    gameLoopJob?.cancel()
    finishShift()
  }

  private fun finishShift() {
    gameLoopJob?.cancel()
    policeLoopJob?.cancel()

    viewModelScope.launch {
      val netProfit = shiftRevenue + shiftTips - shiftIngredientCost - policeFine - damageCost
      val xpEarned = (shiftCustomersServed * 25) + if (escapedPolice) 100 else 40

      val (newLevel, leveledUp) = repository.addXpAndCheckLevelUp(xpEarned)
      didLevelUp = leveledUp
      _leveledUpNotification.value = leveledUp

      val updatedProfile = repository.getProfile().copy(
        cash = _currentCash.value,
        totalSkewersSold = repository.getProfile().totalSkewersSold + shiftCustomersServed,
        totalRevenueAllTime = repository.getProfile().totalRevenueAllTime + shiftRevenue + shiftTips,
        timesEscapedPolice = repository.getProfile().timesEscapedPolice + (if (escapedPolice) 1 else 0),
        timesCaughtByPolice = repository.getProfile().timesCaughtByPolice + (if (policeFine > 0) 1 else 0)
      )
      repository.saveProfile(updatedProfile)

      val report = ShiftReport(
        day = updatedProfile.day,
        location = _shiftSetup.value.locationId,
        customersServed = shiftCustomersServed,
        customersLost = shiftCustomersLost,
        totalRevenue = shiftRevenue,
        totalTips = shiftTips,
        ingredientCost = shiftIngredientCost,
        policeFine = policeFine,
        damageCost = damageCost,
        netProfit = netProfit,
        xpEarned = xpEarned,
        escapedPolice = escapedPolice,
        wasRaidEncountered = wasRaidHit,
        peakRiskPercent = shiftPeakRisk
      )
      _shiftReport.value = report
      _currentScreen.value = AppScreen.SUMMARY
    }
  }

  fun advanceToNextDayShift() {
    viewModelScope.launch {
      val nextTrend = repository.advanceDayAndRollTrend()
      _trendingFood.value = nextTrend
      _currentScreen.value = AppScreen.SETUP
    }
  }

  fun buyUpgrade(upgradeId: UpgradeId) {
    viewModelScope.launch {
      val success = repository.buyUpgrade(upgradeId)
      if (success) {
        soundManager.playCoin()
      } else {
        soundManager.playBurn()
      }
    }
  }

  fun unlockFoodRecipe(foodId: FoodId) {
    viewModelScope.launch {
      val cost = foodId.cost * 6
      val p = repository.getProfile()
      if (p.cash >= cost) {
        repository.addCash(-cost)
        repository.unlockFood(foodId)
        soundManager.playCoin()
      }
    }
  }

  fun resetGame() {
    viewModelScope.launch {
      repository.resetGameData()
      _currentCash.value = 120_000
      _currentScreen.value = AppScreen.MENU
    }
  }

  private fun getUnlockedFoods(): List<FoodId> {
    val unlocked = _playerProfile.value.unlockedFoodsCsv.split(",").filter { it.isNotBlank() }.toSet()
    return FoodId.values().filter { unlocked.contains(it.name) }.ifEmpty { listOf(FoodId.CA_VIEN_CHIEN) }
  }
}
