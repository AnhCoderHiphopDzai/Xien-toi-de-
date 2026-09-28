package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.CashGold
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.DeepNight
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PlasticRedChair
import com.example.ui.theme.PoliceBlue
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningYellow

/**
 * Sự kiện: "Công an tới!" - 10s thần tốc thu dọn đồ đạc!
 */
@Composable
fun PoliceRaidOverlay(
  timeLeftSec: Float,
  tablesPackedPercent: Float,
  kitchenPackedPercent: Float,
  onTapPackTables: () -> Unit,
  onTapPackKitchen: () -> Unit,
  onEscapeCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "siren_alert")
  val flash by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(200, easing = LinearEasing), RepeatMode.Reverse),
    label = "flash"
  )

  val strobeBg = if (flash > 0.5f) PoliceRed.copy(alpha = 0.25f) else PoliceBlue.copy(alpha = 0.25f)
  val isReadyToFlee = tablesPackedPercent >= 1.0f && kitchenPackedPercent >= 1.0f

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxSize()
      .background(strobeBg)
      .padding(16.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DeepNight)
        .border(2.dp, if (flash > 0.5f) PoliceRed else PoliceBlue, RoundedCornerShape(16.dp))
        .padding(20.dp)
    ) {
      // Header Siren
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🚨", fontSize = 28.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "CÔNG AN ĐÔ THỊ TỚI!",
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          color = PoliceRed
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "🚨", fontSize = 28.sp)
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Risk Meter đã chạm trần! Thu dọn tất cả bàn ghế, bếp nấu trước khi hết giờ để tẩu thoát!",
        fontSize = 11.sp,
        color = Color.White,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        lineHeight = 15.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Countdown Timer
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(DarkAlley)
          .border(3.dp, if (timeLeftSec <= 3f) PoliceRed else WarningYellow, CircleShape)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "%.1fs".format(timeLeftSec.coerceAtLeast(0f)),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = if (timeLeftSec <= 3f) PoliceRed else WarningYellow
          )
          Text(text = "CÒN LẠI", fontSize = 8.sp, color = TextPrimary)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Step 1: Pack plastic tables & chairs
      PackingStepRow(
        title = "1. Thu dọn bàn ghế nhựa",
        emoji = "🪑",
        progress = tablesPackedPercent,
        buttonText = if (tablesPackedPercent >= 1.0f) "ĐÃ XẾP XONG!" else "GẤP BÀN GHẾ (CHẠM NHANH!)",
        onTap = onTapPackTables,
        isCompleted = tablesPackedPercent >= 1.0f,
        testTag = "pack_tables_action"
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Step 2: Pack cooking gear & skewers
      PackingStepRow(
        title = "2. Thu gom bếp rán & khay xiên",
        emoji = "🍳",
        progress = kitchenPackedPercent,
        buttonText = if (kitchenPackedPercent >= 1.0f) "ĐÃ GẤP BẾP!" else "CẤT BẾP & ĐỒ (CHẠM NHANH!)",
        onTap = onTapPackKitchen,
        isCompleted = kitchenPackedPercent >= 1.0f,
        testTag = "pack_kitchen_action"
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Final Escape Button
      Button(
        onClick = onEscapeCart,
        enabled = isReadyToFlee,
        colors = ButtonDefaults.buttonColors(
          containerColor = MoneyGreen,
          disabledContainerColor = Color(0xFF37474F)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("flee_now_btn")
      ) {
        Icon(
          imageVector = Icons.Default.DirectionsRun,
          contentDescription = null,
          tint = if (isReadyToFlee) Color.Black else Color.Gray,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isReadyToFlee) "ĐẨY XE TẨU THOÁT VÀO NGÕ!" else "HÃY THU DỌN XONG ĐỂ CHẠY!",
          fontWeight = FontWeight.Black,
          fontSize = 13.sp,
          color = if (isReadyToFlee) Color.Black else Color.Gray
        )
      }
    }
  }
}

@Composable
private fun PackingStepRow(
  title: String,
  emoji: String,
  progress: Float,
  buttonText: String,
  onTap: () -> Unit,
  isCompleted: Boolean,
  testTag: String
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(DarkAlley)
      .padding(10.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = Color.White
        )
      }
      Text(
        text = "${(progress * 100).toInt()}%",
        fontWeight = FontWeight.Black,
        fontSize = 11.sp,
        color = if (isCompleted) MoneyGreen else WarningYellow
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    LinearProgressIndicator(
      progress = { progress.coerceIn(0f, 1f) },
      color = if (isCompleted) MoneyGreen else SizzlingRed,
      trackColor = Color(0xFF263238),
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
    )

    Spacer(modifier = Modifier.height(6.dp))

    Button(
      onClick = onTap,
      enabled = !isCompleted,
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isCompleted) Color(0xFF2E7D32) else SizzlingRed
      ),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(38.dp)
        .testTag(testTag)
    ) {
      Text(
        text = buttonText,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        color = Color.White
      )
    }
  }
}
