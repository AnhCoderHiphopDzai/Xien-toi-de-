package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerProfileEntity
import com.example.data.model.ShiftReport
import com.example.ui.theme.CardBgDark
import com.example.ui.theme.CashGold
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.DeepNight
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow

/**
 * Pha Kết ca: Tổng kết ca bán hàng & Hóa đơn chi tiết
 */
@Composable
fun ShiftSummaryScreen(
  report: ShiftReport,
  profile: PlayerProfileEntity,
  leveledUp: Boolean,
  onContinueToNextShift: () -> Unit,
  onGoToShop: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNight)
      .verticalScroll(scrollState)
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header
    Icon(
      imageVector = Icons.Default.Receipt,
      contentDescription = null,
      tint = CashGold,
      modifier = Modifier.size(36.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "TỔNG KẾT CA BÁN HÀNG",
      fontWeight = FontWeight.Black,
      fontSize = 18.sp,
      color = SkewerOrange
    )
    Text(
      text = "Ngày thứ ${report.day} • ${report.location.displayName}",
      fontSize = 12.sp,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Level Up Celebration Banner
    if (leveledUp) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFE65100))
          .padding(12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "⭐", fontSize = 24.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "CHÚC MỪNG BẠN ĐÃ LÊN CẤP ${profile.level}!",
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = WarningYellow
            )
            Text(
              text = "Mở khóa thêm địa điểm mới và nâng cấp đồ nghề xịn hơn tại Chợ Đồ Nghề!",
              fontSize = 11.sp,
              color = Color.White
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(12.dp))
    }

    // Receipt Card
    Card(
      colors = CardDefaults.cardColors(containerColor = DarkAlley),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF37474F)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "CHI TIẾT DOANH THU & CHI PHÍ",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = CashGold
        )
        Spacer(modifier = Modifier.height(10.dp))

        ReceiptLine(
          label = "Khách đã phục vụ ngon miệng",
          value = "${report.customersServed} lượt",
          color = MoneyGreen
        )
        ReceiptLine(
          label = "Khách bỏ về do đợi lâu",
          value = "${report.customersLost} lượt",
          color = if (report.customersLost > 0) PoliceRed else TextSecondary
        )
        ReceiptLine(
          label = "Tổng doanh thu bán xiên",
          value = "+%,d đ".format(report.totalRevenue),
          color = CashGold
        )
        ReceiptLine(
          label = "Tiền tip khách thưởng",
          value = "+%,d đ".format(report.totalTips),
          color = MoneyGreen
        )

        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF37474F)))
        Spacer(modifier = Modifier.height(6.dp))

        ReceiptLine(
          label = "Tiền vốn nguyên liệu xiên",
          value = "-%,d đ".format(report.ingredientCost),
          color = Color(0xFFFF8A80)
        )

        if (report.policeFine > 0) {
          ReceiptLine(
            label = "Phạt vi phạm trật tự đô thị",
            value = "-%,d đ".format(report.policeFine),
            color = PoliceRed
          )
        }

        if (report.damageCost > 0) {
          ReceiptLine(
            label = "Thiệt hại xe quẹt va chạm",
            value = "-%,d đ".format(report.damageCost),
            color = PoliceRed
          )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF37474F)))
        Spacer(modifier = Modifier.height(6.dp))

        // Net Profit Highlight
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "LỢI NHUẬN RÒNG:",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = Color.White
          )
          Text(
            text = "${if (report.netProfit >= 0) "+" else ""}${ "%,d đ".format(report.netProfit) }",
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            color = if (report.netProfit >= 0) MoneyGreen else PoliceRed
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // XP Progress
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = "Kinh nghiệm tích lũy (EXP):", fontSize = 11.sp, color = TextSecondary)
          Text(
            text = "+${report.xpEarned} XP",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = WarningYellow
          )
        }

        // Peak Risk
        Row(
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(text = "Mức độ chú ý đô thị đỉnh điểm:", fontSize = 11.sp, color = TextSecondary)
          Text(
            text = "${report.peakRiskPercent}%",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (report.peakRiskPercent > 75) PoliceRed else WarningYellow
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Total Wallet Balance
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(DarkAlley)
        .border(1.dp, CashGold, RoundedCornerShape(12.dp))
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Text(text = "Số dư ví hiện tại: ", fontSize = 13.sp, color = TextPrimary)
      Text(
        text = "%,d đ".format(profile.cash),
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        color = CashGold
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Actions
    Row(
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      OutlinedButton(
        onClick = onGoToShop,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CashGold),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("summary_open_shop_btn")
      ) {
        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "NÂNG CẤP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = onContinueToNextShift,
        colors = ButtonDefaults.buttonColors(containerColor = SizzlingRed),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .weight(1.5f)
          .height(48.dp)
          .testTag("continue_next_shift_btn")
      ) {
        Text(text = "CA TIẾP THEO", fontWeight = FontWeight.Black, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.Default.ArrowForward, contentDescription = null)
      }
    }
  }
}

@Composable
private fun ReceiptLine(
  label: String,
  value: String,
  color: Color
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp)
  ) {
    Text(text = label, fontSize = 11.sp, color = TextSecondary)
    Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
  }
}
