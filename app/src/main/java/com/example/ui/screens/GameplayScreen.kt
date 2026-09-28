package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CookingSlot
import com.example.data.model.CustomerOrder
import com.example.data.model.CustomerType
import com.example.data.model.FoodId
import com.example.data.model.FoodTrend
import com.example.data.model.PlatedItem
import com.example.data.model.SauceType
import com.example.data.model.ShiftSetup
import com.example.ui.components.CookingStationView
import com.example.ui.components.CustomerAvatarBox
import com.example.ui.components.PoliceWarningBanner
import com.example.ui.components.RiskMeterBar
import com.example.ui.theme.CardBgDark
import com.example.ui.theme.CashGold
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.DeepNight
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.ShipperGreen
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow

/**
 * Pha Bán Hàng: Nhận đơn, chiên nướng, phục vụ khách, lách trật tự đô thị!
 */
@Composable
fun GameplayScreen(
  shiftSetup: ShiftSetup,
  clockHourText: String, // e.g. "17:30"
  cashAmount: Int,
  dayNumber: Int,
  riskPercent: Float,
  tableRisk: Float,
  speakerRisk: Float,
  trashRisk: Float,
  shipperRisk: Float,
  trashAmount: Float,
  activeOrders: List<CustomerOrder>,
  cookingSlots: List<CookingSlot>,
  platedItems: List<PlatedItem>,
  unlockedFoods: Set<FoodId>,
  trendingFood: FoodTrend?,
  isSpeakerMuted: Boolean,
  onToggleSpeaker: () -> Unit,
  onAddFoodToCook: (FoodId) -> Unit,
  onPickupCookedFood: (slotId: Int) -> Unit,
  onDiscardBurnt: (slotId: Int) -> Unit,
  onSelectSauceForPlated: (plateIndex: Int, SauceType) -> Unit,
  onDeliverPlate: (plateIndex: Int) -> Unit,
  onClearTrash: () -> Unit,
  onEndShiftManually: () -> Unit,
  modifier: Modifier = Modifier
) {
  val verticalScroll = rememberScrollState()
  val customerScroll = rememberScrollState()

  val isCriticalRisk = riskPercent >= 80f
  val shipperCount = activeOrders.count { it.customerType.isShipper }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNight)
      .verticalScroll(verticalScroll)
      .padding(12.dp)
  ) {
    // Top Bar: Clock, Location, Speaker Toggle, Cash
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AccessTime,
          contentDescription = null,
          tint = GoldenCrisp,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = clockHourText,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "• Ngày $dayNumber",
          fontSize = 11.sp,
          color = TextSecondary
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Speaker Mute/Unmute
        IconButton(
          onClick = onToggleSpeaker,
          modifier = Modifier
            .size(32.dp)
            .testTag("toggle_speaker_btn")
        ) {
          Icon(
            imageVector = if (isSpeakerMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
            contentDescription = "Loa kéo",
            tint = if (isSpeakerMuted) TextMuted else SkewerOrange,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Wallet
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkAlley)
            .border(1.dp, CashGold, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(text = "💵 ", fontSize = 11.sp)
          Text(
            text = "%,d đ".format(cashAmount),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = CashGold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Police Warning Strobe Banner if risk is critical
    if (isCriticalRisk) {
      PoliceWarningBanner(modifier = Modifier.padding(bottom = 6.dp))
    }

    // Risk Meter Bar
    RiskMeterBar(
      riskPercent = riskPercent,
      tableRisk = tableRisk,
      speakerRisk = speakerRisk,
      trashRisk = trashRisk,
      shipperRisk = shipperRisk,
      patrolRate = shiftSetup.locationId.basePatrolRate
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Active Customer Orders Section
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "KHÁCH ĐANG ĐỢI (${activeOrders.size})",
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          color = SkewerOrange
        )
        if (shipperCount > 1) {
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(ShipperGreen)
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = "$shipperCount Shipper (lấn vỉa hè!)",
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
        }
      }

      OutlinedButton(
        onClick = onEndShiftManually,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
          .height(28.dp)
          .testTag("close_shift_early_btn")
      ) {
        Text(text = "Dọn hàng nghỉ ca", fontSize = 9.sp)
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Customer Cards Horizontal Scroll
    if (activeOrders.isEmpty()) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .height(72.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(DarkAlley)
          .border(1.dp, Color(0xFF37474F), RoundedCornerShape(10.dp))
      ) {
        Text(
          text = "Đang đón khách tới quầy...",
          fontSize = 11.sp,
          color = TextMuted
        )
      }
    } else {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(customerScroll)
      ) {
        activeOrders.forEach { order ->
          CustomerOrderBubbleCard(order = order)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Cooking Stations and Plating View
    CookingStationView(
      cookingSlots = cookingSlots,
      platedItems = platedItems,
      unlockedFoods = unlockedFoods,
      trashAmount = trashAmount,
      onAddFoodToCook = onAddFoodToCook,
      onPickupCookedFood = onPickupCookedFood,
      onDiscardBurnt = onDiscardBurnt,
      onSelectSauceForPlated = onSelectSauceForPlated,
      onDeliverPlate = onDeliverPlate,
      onClearTrash = onClearTrash
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun CustomerOrderBubbleCard(order: CustomerOrder) {
  val patienceFraction = order.patienceFraction
  val isUrgent = patienceFraction < 0.25f

  Card(
    colors = CardDefaults.cardColors(containerColor = DarkAlley),
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isUrgent) 1.5.dp else 1.dp,
      color = if (isUrgent) PoliceRed else Color(0xFF37474F)
    ),
    modifier = Modifier
      .width(170.dp)
      .testTag("order_card_${order.id}")
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          CustomerAvatarBox(
            customerType = order.customerType,
            patienceFraction = patienceFraction,
            modifier = Modifier.size(34.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = order.customerType.displayName,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = if (order.customerType.isShipper) ShipperGreen else TextPrimary,
              maxLines = 1
            )
            Text(
              text = order.tableName ?: "Mang về",
              fontSize = 8.sp,
              color = TextMuted
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Patience Progress Bar
      LinearProgressIndicator(
        progress = { patienceFraction },
        color = if (patienceFraction > 0.5f) MoneyGreen else if (patienceFraction > 0.25f) WarningYellow else PoliceRed,
        trackColor = Color(0xFF263238),
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(RoundedCornerShape(1.5.dp))
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Requested Items List
      Text(
        text = "Món gọi:",
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary
      )
      order.items.forEach { food ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 1.dp)
        ) {
          Text(text = food.emoji, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = food.displayName,
            fontSize = 9.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Sauce requirement
      if (order.requiredSauce != SauceType.NONE) {
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = order.requiredSauce.emoji, fontSize = 11.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Kèm: ${order.requiredSauce.displayName}",
            fontSize = 8.sp,
            color = WarningYellow
          )
        }
      }
    }
  }
}
