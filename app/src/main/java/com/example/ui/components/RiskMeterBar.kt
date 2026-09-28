package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CardBgDark
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow

/**
 * Arcade Risk Meter with Police Threat Breakdown
 */
@Composable
fun RiskMeterBar(
  riskPercent: Float, // 0f to 100f
  tableRisk: Float,
  speakerRisk: Float,
  trashRisk: Float,
  shipperRisk: Float,
  patrolRate: Float,
  modifier: Modifier = Modifier
) {
  var showDetails by remember { mutableStateOf(false) }

  val transition = rememberInfiniteTransition(label = "risk_pulse")
  val pulse by transition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
    label = "pulse"
  )

  val isCritical = riskPercent >= 80f
  val barColor = when {
    riskPercent < 45f -> Brush.horizontalGradient(listOf(MoneyGreen, WarningYellow))
    riskPercent < 75f -> Brush.horizontalGradient(listOf(WarningYellow, SkewerOrange))
    else -> Brush.horizontalGradient(listOf(SkewerOrange, PoliceRed))
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(DarkAlley)
      .border(
        width = if (isCritical) 2.dp else 1.dp,
        color = if (isCritical) PoliceRed.copy(alpha = pulse.coerceIn(0.6f, 1f)) else Color(0xFF37474F),
        shape = RoundedCornerShape(10.dp)
      )
      .padding(10.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (isCritical) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Nguy hiểm",
            tint = PoliceRed,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
          text = "NGUY CƠ ĐÔ THỊ (RISK METER)",
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = if (isCritical) PoliceRed else TextSecondary
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .testTag("toggle_risk_info_btn")
          .clickable { showDetails = !showDetails }
      ) {
        Text(
          text = "${riskPercent.toInt()}%",
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          color = if (isCritical) PoliceRed else WarningYellow
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Chi tiết nguy cơ",
          tint = TextMuted,
          modifier = Modifier.size(15.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Progress Track
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(14.dp)
        .clip(RoundedCornerShape(7.dp))
        .background(Color(0xFF101014))
        .border(1.dp, Color(0xFF263238), RoundedCornerShape(7.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth((riskPercent / 100f).coerceIn(0f, 1f))
          .clip(RoundedCornerShape(7.dp))
          .background(barColor)
      )
    }

    // Expandable Breakdown
    AnimatedVisibility(visible = showDetails) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(CardBgDark)
          .padding(8.dp)
      ) {
        Text(
          text = "Nguồn làm tăng mức chú ý của Trật Tự Đô Thị:",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        RiskDetailRow(label = "🪑 Bàn ghế lấn chiếm vỉa hè", value = "+${tableRisk.toInt()}%")
        RiskDetailRow(label = "📢 Âm lượng loa kéo", value = "+${speakerRisk.toInt()}%")
        RiskDetailRow(label = "🗑️ Rác thải / Vệt dầu chưa dọn", value = "+${trashRisk.toInt()}%")
        RiskDetailRow(label = "🛵 Shipper đứng chờ gom đơn", value = "+${shipperRisk.toInt()}%")
        RiskDetailRow(label = "👮 Tần suất tuần tra địa bàn", value = "x${"%.1f".format(patrolRate)}")
      }
    }
  }
}

@Composable
private fun RiskDetailRow(label: String, value: String) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp)
  ) {
    Text(text = label, fontSize = 10.sp, color = TextSecondary)
    Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarningYellow)
  }
}
