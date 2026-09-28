package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.GetawayMiniGameScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.PoliceRaidOverlay
import com.example.ui.screens.ShiftSetupScreen
import com.example.ui.screens.ShiftSummaryScreen
import com.example.ui.screens.UpgradesShopScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: GameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
        ) { innerPadding ->
          GameApp(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

@Composable
fun GameApp(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val profile by viewModel.playerProfile.collectAsStateWithLifecycle()
  val trendingFood by viewModel.trendingFood.collectAsStateWithLifecycle()
  val shiftSetup by viewModel.shiftSetup.collectAsStateWithLifecycle()
  val clockTimeText by viewModel.clockTimeText.collectAsStateWithLifecycle()
  val currentCash by viewModel.currentCash.collectAsStateWithLifecycle()
  val riskPercent by viewModel.riskPercent.collectAsStateWithLifecycle()
  val tableRisk by viewModel.tableRisk.collectAsStateWithLifecycle()
  val speakerRisk by viewModel.speakerRisk.collectAsStateWithLifecycle()
  val trashRisk by viewModel.trashRisk.collectAsStateWithLifecycle()
  val shipperRisk by viewModel.shipperRisk.collectAsStateWithLifecycle()
  val trashAmount by viewModel.trashAmount.collectAsStateWithLifecycle()
  val activeOrders by viewModel.activeOrders.collectAsStateWithLifecycle()
  val cookingSlots by viewModel.cookingSlots.collectAsStateWithLifecycle()
  val platedItems by viewModel.platedItems.collectAsStateWithLifecycle()
  val isSpeakerMuted by viewModel.isSpeakerMuted.collectAsStateWithLifecycle()
  val raidTimeLeftSec by viewModel.raidTimeLeftSec.collectAsStateWithLifecycle()
  val tablesPackedPercent by viewModel.tablesPackedPercent.collectAsStateWithLifecycle()
  val kitchenPackedPercent by viewModel.kitchenPackedPercent.collectAsStateWithLifecycle()
  val shiftReport by viewModel.shiftReport.collectAsStateWithLifecycle()
  val leveledUpNotification by viewModel.leveledUpNotification.collectAsStateWithLifecycle()

  // Handle Back Navigation
  BackHandler(enabled = currentScreen != AppScreen.MENU) {
    when (currentScreen) {
      AppScreen.SETUP -> viewModel.setScreen(AppScreen.MENU)
      AppScreen.SHOP -> viewModel.setScreen(AppScreen.SETUP)
      AppScreen.SUMMARY -> viewModel.setScreen(AppScreen.SETUP)
      AppScreen.GAMEPLAY -> viewModel.endShiftManually()
      else -> viewModel.setScreen(AppScreen.MENU)
    }
  }

  val unlockedFoods = profile.unlockedFoodsCsv.split(",")
    .filter { it.isNotBlank() }
    .mapNotNull {
      try {
        com.example.data.model.FoodId.valueOf(it)
      } catch (_: Exception) {
        null
      }
    }
    .toSet()

  Box(modifier = modifier.fillMaxSize()) {
    when (currentScreen) {
      AppScreen.MENU -> {
        MenuScreen(
          profile = profile,
          onStartGame = { viewModel.setScreen(AppScreen.SETUP) },
          onResetGame = { viewModel.resetGame() }
        )
      }

      AppScreen.SETUP -> {
        ShiftSetupScreen(
          profile = profile,
          trendingFood = trendingFood,
          onStartShift = { setup -> viewModel.startShift(setup) },
          onOpenShop = { viewModel.setScreen(AppScreen.SHOP) }
        )
      }

      AppScreen.GAMEPLAY -> {
        GameplayScreen(
          shiftSetup = shiftSetup,
          clockHourText = clockTimeText,
          cashAmount = currentCash,
          dayNumber = profile.day,
          riskPercent = riskPercent,
          tableRisk = tableRisk,
          speakerRisk = speakerRisk,
          trashRisk = trashRisk,
          shipperRisk = shipperRisk,
          trashAmount = trashAmount,
          activeOrders = activeOrders,
          cookingSlots = cookingSlots,
          platedItems = platedItems,
          unlockedFoods = unlockedFoods,
          trendingFood = trendingFood,
          isSpeakerMuted = isSpeakerMuted,
          onToggleSpeaker = { viewModel.toggleSpeaker() },
          onAddFoodToCook = { food -> viewModel.addFoodToCook(food) },
          onPickupCookedFood = { slotId -> viewModel.pickupCookedFood(slotId) },
          onDiscardBurnt = { slotId -> viewModel.discardBurnt(slotId) },
          onSelectSauceForPlated = { index, sauce -> viewModel.selectSauceForPlated(index, sauce) },
          onDeliverPlate = { index -> viewModel.deliverPlate(index) },
          onClearTrash = { viewModel.clearTrash() },
          onEndShiftManually = { viewModel.endShiftManually() }
        )
      }

      AppScreen.POLICE_RAID -> {
        // Overlay for frantic police packing
        PoliceRaidOverlay(
          timeLeftSec = raidTimeLeftSec,
          tablesPackedPercent = tablesPackedPercent,
          kitchenPackedPercent = kitchenPackedPercent,
          onTapPackTables = { viewModel.tapPackTables() },
          onTapPackKitchen = { viewModel.tapPackKitchen() },
          onEscapeCart = { viewModel.escapeCartMiniGame() }
        )
      }

      AppScreen.GETAWAY_MINIGAME -> {
        GetawayMiniGameScreen(
          soundManager = viewModel.soundManager,
          hasTurboCart = profile.ownedUpgradesCsv.contains(com.example.data.model.UpgradeId.TURBO_CART.name),
          onFinishMiniGame = { success, damage, bonus ->
            viewModel.finishGetawayMiniGame(success, damage, bonus)
          }
        )
      }

      AppScreen.SUMMARY -> {
        val report = shiftReport
        if (report != null) {
          ShiftSummaryScreen(
            report = report,
            profile = profile,
            leveledUp = leveledUpNotification,
            onContinueToNextShift = { viewModel.advanceToNextDayShift() },
            onGoToShop = { viewModel.setScreen(AppScreen.SHOP) }
          )
        } else {
          viewModel.setScreen(AppScreen.SETUP)
        }
      }

      AppScreen.SHOP -> {
        UpgradesShopScreen(
          profile = profile,
          onBuyUpgrade = { upg -> viewModel.buyUpgrade(upg) },
          onUnlockFoodRecipe = { food -> viewModel.unlockFoodRecipe(food) },
          onBack = { viewModel.setScreen(AppScreen.SETUP) }
        )
      }
    }
  }
}
