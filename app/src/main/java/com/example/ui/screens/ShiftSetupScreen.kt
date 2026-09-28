package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodId
import com.example.data.model.FoodTrend
import com.example.data.model.LocationId
import com.example.data.model.PlayerProfileEntity
import com.example.data.model.ShiftSetup
import com.example.data.model.SpeakerLevel
import com.example.ui.components.HanoiStreetFurnitureGraphic
import com.example.ui.theme.CardBgDark
import com.example.ui.theme.CashGold
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.DeepNight
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PlasticBlueTable
import com.example.ui.theme.PlasticRedChair
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow

/**
 * Pha Setup: Chuẩn bị quầy & Dọn hàng trước ca bán
 */
@Composable
fun ShiftSetupScreen(
  profile: PlayerProfileEntity,
  trendingFood: FoodTrend?,
  onStartShift: (ShiftSetup) -> Unit,
  onOpenShop: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedLocation by remember { mutableStateOf(LocationId.CONG_TRUONG) }
  var tableCount by remember { mutableIntStateOf(2) }
  var speakerLevel by remember { mutableStateOf(SpeakerLevel.LOW) }

  val maxTables = 5
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNight)
      .verticalScroll(scrollState)
      .padding(16.dp)
  ) {
    // Header Bar: Player Info & Wallet
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        Text(
          text = "XIÊN TÔI ĐÊ! - HÀ NỘI",
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          color = SkewerOrange
        )
        Text(
          text = "Ngày ${profile.day} • Cấp ${profile.level} (${profile.playerName})",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(DarkAlley)
          .border(1.dp, CashGold, RoundedCornerShape(20.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(text = "💵 ", fontSize = 14.sp)
        Text(
          text = "%,d đ".format(profile.cash),
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = CashGold
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Trending Banner
    if (trendingFood != null) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFFB71C1C), Color(0xFFE65100))
            )
          )
          .padding(10.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🔥", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "HOT TREND TOÀN THÀNH PHỐ!",
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              color = WarningYellow
            )
            Text(
              text = "Món: ${trendingFood.foodId.displayName} (${trendingFood.foodId.emoji}) được săn đón! Giá bán +50% (còn ${trendingFood.remainingDays} ngày)",
              fontSize = 11.sp,
              color = Color.White
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Step 1: Choose Location
    Text(
      text = "1. CHỌN ĐỊA ĐIỂM BÁN HÀNG",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = CashGold
    )
    Spacer(modifier = Modifier.height(6.dp))

    LocationId.values().forEach { loc ->
      val isUnlocked = profile.level >= loc.unlockLevel
      val isSelected = selectedLocation == loc

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (isSelected) Color(0xFF263238) else DarkAlley
        ),
        border = androidx.compose.foundation.BorderStroke(
          width = if (isSelected) 2.dp else 1.dp,
          color = if (isSelected) SkewerOrange else Color(0xFF37474F)
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable(enabled = isUnlocked) { selectedLocation = loc }
          .testTag("location_select_${loc.name}")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) SizzlingRed else Color(0xFF37474F))
          ) {
            Icon(
              imageVector = if (isUnlocked) Icons.Default.LocationOn else Icons.Default.Lock,
              contentDescription = null,
              tint = Color.White
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = loc.displayName,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = TextPrimary
            )
            Text(
              text = loc.description,
              fontSize = 10.sp,
              color = TextSecondary,
              lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Tuần tra: x${"%.1f".format(loc.basePatrolRate)} • Lợi nhuận vé: x${"%.1f".format(loc.avgTicketMultiplier)}",
              fontSize = 9.sp,
              color = if (loc.basePatrolRate > 1.5f) PoliceRed else WarningYellow,
              fontWeight = FontWeight.SemiBold
            )
          }

          if (!isUnlocked) {
            Text(
              text = "Mở khóa ở Cấp ${loc.unlockLevel}",
              fontSize = 9.sp,
              color = PoliceRed,
              fontWeight = FontWeight.Bold
            )
          } else if (isSelected) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Đã chọn",
              tint = SkewerOrange
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Step 2: Configure Stools & Plastic Tables
    Text(
      text = "2. SẮP XẾP BÀN GHẾ NHỰA VỈA HÈ",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = CashGold
    )
    Text(
      text = "Bày càng nhiều bàn ghế = Đông khách ngồi ăn, nhưng lấn chiếm vỉa hè khiến Risk Meter tăng nhanh!",
      fontSize = 10.sp,
      color = TextSecondary
    )
    Spacer(modifier = Modifier.height(6.dp))

    Card(
      colors = CardDefaults.cardColors(containerColor = DarkAlley),
      shape = RoundedCornerShape(10.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF37474F)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Số lượng bộ bàn ghế nhựa: $tableCount bộ",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = TextPrimary
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { if (tableCount > 0) tableCount-- },
              modifier = Modifier.size(36.dp).testTag("decrease_tables_btn")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Bớt", tint = TextPrimary)
            }
            Text(
              text = "$tableCount",
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              color = PlasticBlueTable
            )
            IconButton(
              onClick = { if (tableCount < maxTables) tableCount++ },
              modifier = Modifier.size(36.dp).testTag("increase_tables_btn")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Thêm", tint = TextPrimary)
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Visual Preview of Street Furniture
        HanoiStreetFurnitureGraphic(
          tableCount = tableCount,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Mức độ lấn vỉa hè: +${tableCount * 12}% Risk",
            fontSize = 10.sp,
            color = if (tableCount > 3) PoliceRed else WarningYellow,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Sức chứa khách ăn: ${tableCount * 2} người",
            fontSize = 10.sp,
            color = MoneyGreen
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Step 3: Loudspeaker Setup
    Text(
      text = "3. LOA KÉO PHÁT NHẠC HÚT KHÁCH",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = CashGold
    )
    Spacer(modifier = Modifier.height(6.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      SpeakerLevel.values().forEach { level ->
        val isSelected = speakerLevel == level
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SizzlingRed else DarkAlley)
            .border(
              width = 1.dp,
              color = if (isSelected) CashGold else Color(0xFF37474F),
              shape = RoundedCornerShape(8.dp)
            )
            .clickable { speakerLevel = level }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("speaker_${level.name}")
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📢", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = level.label,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Khách x${"%.2f".format(level.crowdBoost)}",
              fontSize = 8.sp,
              color = WarningYellow
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Action Buttons: Shop & Start Shift
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      OutlinedButton(
        onClick = onOpenShop,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CashGold),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("open_shop_btn")
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "NÂNG CẤP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = {
          onStartShift(
            ShiftSetup(
              locationId = selectedLocation,
              tableCount = tableCount,
              speakerVolume = speakerLevel
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = SizzlingRed),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .weight(1.5f)
          .height(48.dp)
          .testTag("start_shift_btn")
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "MỞ BÁN CA HÀNG!",
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
