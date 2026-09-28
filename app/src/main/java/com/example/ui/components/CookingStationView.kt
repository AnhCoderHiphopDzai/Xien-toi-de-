package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CookingSlot
import com.example.data.model.CookingStationType
import com.example.data.model.CookingStatus
import com.example.data.model.FoodId
import com.example.data.model.PlatedItem
import com.example.data.model.SauceType
import com.example.ui.theme.BurntDark
import com.example.ui.theme.CardBgDark
import com.example.ui.theme.CashGold
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SidewalkGray
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow

/**
 * Cooking Stations & Plating Board
 */
@Composable
fun CookingStationView(
  cookingSlots: List<CookingSlot>,
  platedItems: List<PlatedItem>,
  unlockedFoods: Set<FoodId>,
  trashAmount: Float, // 0f to 100f
  onAddFoodToCook: (FoodId) -> Unit,
  onPickupCookedFood: (slotId: Int) -> Unit,
  onDiscardBurnt: (slotId: Int) -> Unit,
  onSelectSauceForPlated: (plateIndex: Int, SauceType) -> Unit,
  onDeliverPlate: (plateIndex: Int) -> Unit,
  onClearTrash: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(DarkAlley)
      .border(1.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
      .padding(10.dp)
  ) {
    // Top Bar: Cooking Stations Label + Sidewalk Trash Cleanup Button
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.LocalFireDepartment,
          contentDescription = null,
          tint = SizzlingRed,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "BẾP NẤU & KHAY ĐĨA",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = TextPrimary
        )
      }

      // Trash Cleanup Action
      val trashCritical = trashAmount > 40f
      Button(
        onClick = onClearTrash,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (trashCritical) PoliceRed else Color(0xFF37474F)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .height(34.dp)
          .testTag("clean_trash_btn")
      ) {
        Icon(
          imageVector = Icons.Default.CleaningServices,
          contentDescription = "Dọn rác",
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (trashAmount > 5f) "Quét rác (${trashAmount.toInt()}%)" else "Vỉa hè sạch",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Cooking Slots Row (Fryer slots + Stone Grill slots + Drink slot)
    Text(
      text = "Trạng thái bếp nấu (Nhấn lấy đồ khi chín vàng):",
      fontSize = 10.sp,
      color = TextSecondary
    )
    Spacer(modifier = Modifier.height(4.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      cookingSlots.forEach { slot ->
        CookingSlotCard(
          slot = slot,
          onPickup = { onPickupCookedFood(slot.slotId) },
          onDiscard = { onDiscardBurnt(slot.slotId) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Raw Ingredients Quick Pick to Drop into Bếp
    Text(
      text = "Chọn xiên sống để thả vào bếp:",
      fontSize = 10.sp,
      color = TextSecondary
    )
    Spacer(modifier = Modifier.height(4.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      val fryList = listOf(FoodId.CA_VIEN_CHIEN, FoodId.XUC_XICH_RAN, FoodId.NEM_CHUA_RAN, FoodId.KHOAI_LOC_XOAY)
      fryList.filter { unlockedFoods.contains(it) }.forEach { food ->
        QuickCookFoodChip(
          foodId = food,
          onClick = { onAddFoodToCook(food) },
          modifier = Modifier.weight(1f)
        )
      }

      if (unlockedFoods.contains(FoodId.LAP_XUONG_NUONG_DA)) {
        QuickCookFoodChip(
          foodId = FoodId.LAP_XUONG_NUONG_DA,
          onClick = { onAddFoodToCook(FoodId.LAP_XUONG_NUONG_DA) },
          modifier = Modifier.weight(1f)
        )
      }

      if (unlockedFoods.contains(FoodId.TRA_CHANH)) {
        QuickCookFoodChip(
          foodId = FoodId.TRA_CHANH,
          onClick = { onAddFoodToCook(FoodId.TRA_CHANH) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Plating Area: Ready Food on Plate + Sauce Selection + Serve Button
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = "Khay đồ chín (${platedItems.size}/3 đĩa) - Rưới sốt & Giao khách:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = CashGold
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    if (platedItems.isEmpty()) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(CardBgDark)
          .border(1.dp, Color(0xFF263238), RoundedCornerShape(8.dp))
      ) {
        Text(
          text = "Khay trống. Hãy chiên/nướng món và gắp lên đĩa!",
          fontSize = 11.sp,
          color = TextMuted
        )
      }
    } else {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        platedItems.forEachIndexed { index, item ->
          PlatedItemRow(
            item = item,
            onSelectSauce = { sauce -> onSelectSauceForPlated(index, sauce) },
            onDeliver = { onDeliverPlate(index) }
          )
        }
      }
    }
  }
}

@Composable
private fun CookingSlotCard(
  slot: CookingSlot,
  onPickup: () -> Unit,
  onDiscard: () -> Unit,
  modifier: Modifier = Modifier
) {
  val stationTitle = when (slot.stationType) {
    CookingStationType.FRYER -> "Chảo Chiên"
    CookingStationType.STONE_GRILL -> "Bếp Sỏi"
    CookingStationType.DRINK_STATION -> "Bình Trà"
  }

  val borderColor by animateColorAsState(
    targetValue = when (slot.status) {
      CookingStatus.RAW -> Color(0xFF37474F)
      CookingStatus.COOKING -> WarningYellow
      CookingStatus.DONE -> MoneyGreen
      CookingStatus.BURNT -> PoliceRed
    },
    label = "slot_border"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(CardBgDark)
      .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
      .clickable(enabled = slot.foodId != null) {
        if (slot.status == CookingStatus.DONE) {
          onPickup()
        } else if (slot.status == CookingStatus.BURNT) {
          onDiscard()
        }
      }
      .padding(6.dp)
      .testTag("cooking_slot_${slot.slotId}")
  ) {
    Text(
      text = stationTitle,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = TextMuted
    )

    Spacer(modifier = Modifier.height(4.dp))

    val currentFood = slot.foodId
    if (currentFood != null) {
      FoodSkewerGraphic(
        foodId = currentFood,
        status = slot.status,
        modifier = Modifier.size(38.dp)
      )

      Text(
        text = currentFood.displayName,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(2.dp))

      when (slot.status) {
        CookingStatus.COOKING -> {
          val progress = (slot.progressSec / slot.targetSec).coerceIn(0f, 1f)
          LinearProgressIndicator(
            progress = { progress },
            color = WarningYellow,
            trackColor = Color(0xFF263238),
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp))
          )
          Text(text = "Đang nấu...", fontSize = 8.sp, color = WarningYellow)
        }
        CookingStatus.DONE -> {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(MoneyGreen)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(text = "GẮP ĐĨA!", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
          }
        }
        CookingStatus.BURNT -> {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(PoliceRed)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(text = "CHÁY! VỨT", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
          }
        }
        else -> Unit
      }
    } else {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(38.dp)
      ) {
        Text(text = "Trống", fontSize = 9.sp, color = TextMuted)
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(text = "Thả xiên", fontSize = 8.sp, color = TextMuted)
    }
  }
}

@Composable
private fun QuickCookFoodChip(
  foodId: FoodId,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(SidewalkGray)
      .clickable { onClick() }
      .padding(vertical = 4.dp, horizontal = 2.dp)
      .testTag("quick_cook_${foodId.name}")
  ) {
    Text(text = foodId.emoji, fontSize = 16.sp)
    Text(
      text = foodId.displayName,
      fontSize = 8.sp,
      maxLines = 1,
      fontWeight = FontWeight.Medium,
      color = TextPrimary
    )
    Text(
      text = "${foodId.cost / 1000}k vốn",
      fontSize = 7.sp,
      color = CashGold
    )
  }
}

@Composable
private fun PlatedItemRow(
  item: PlatedItem,
  onSelectSauce: (SauceType) -> Unit,
  onDeliver: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(CardBgDark)
      .border(1.dp, Color(0xFF37474F), RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(text = item.foodId.emoji, fontSize = 20.sp)
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = item.foodId.displayName,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = TextPrimary
        )
        Text(
          text = "Sốt: ${item.sauce.displayName}",
          fontSize = 9.sp,
          color = if (item.sauce != SauceType.NONE) ChiliRed else TextMuted
        )
      }
    }

    // Sauce Buttons
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      SauceMiniButton(
        emoji = "🌶️",
        isSelected = item.sauce == SauceType.TUONG_OT,
        onClick = { onSelectSauce(SauceType.TUONG_OT) }
      )
      SauceMiniButton(
        emoji = "🍯",
        isSelected = item.sauce == SauceType.SOT_ME,
        onClick = { onSelectSauce(SauceType.SOT_ME) }
      )
      SauceMiniButton(
        emoji = "🍅",
        isSelected = item.sauce == SauceType.TUONG_CA,
        onClick = { onSelectSauce(SauceType.TUONG_CA) }
      )

      Spacer(modifier = Modifier.width(4.dp))

      // Deliver Action Button
      Button(
        onClick = onDeliver,
        colors = ButtonDefaults.buttonColors(containerColor = MoneyGreen),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .height(30.dp)
          .testTag("deliver_food_btn")
      ) {
        Text(
          text = "GIAO",
          color = Color.Black,
          fontWeight = FontWeight.Black,
          fontSize = 9.sp
        )
      }
    }
  }
}

@Composable
private fun SauceMiniButton(
  emoji: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(28.dp)
      .clip(RoundedCornerShape(4.dp))
      .background(if (isSelected) SizzlingRed else Color(0xFF2C2D35))
      .border(
        width = 1.dp,
        color = if (isSelected) CashGold else Color.Transparent,
        shape = RoundedCornerShape(4.dp)
      )
      .clickable { onClick() }
  ) {
    Text(text = emoji, fontSize = 12.sp)
  }
}
