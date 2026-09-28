package com.example.ui.screens

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerProfileEntity
import com.example.ui.components.FoodSkewerGraphic
import com.example.ui.theme.CardBgDark
import com.example.ui.theme.CashGold
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.DeepNight
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningYellow

/**
 * Màn hình Chính / Trang Chủ (Main Menu)
 */
@Composable
fun MenuScreen(
  profile: PlayerProfileEntity,
  onStartGame: () -> Unit,
  onResetGame: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showGuideDialog by remember { mutableStateOf(false) }
  var showStatsDialog by remember { mutableStateOf(false) }
  var showResetConfirm by remember { mutableStateOf(false) }
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNight)
      .verticalScroll(scrollState)
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Spacer(modifier = Modifier.height(20.dp))

    // Retro Arcade Signboard
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFFD84315), Color(0xFFB71C1C), Color(0xFF3E2723))
          )
        )
        .border(2.dp, CashGold, RoundedCornerShape(16.dp))
        .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "🍢", fontSize = 28.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "XIÊN TÔI ĐÊ!!!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = WarningYellow
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "🌭", fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "VUA XIÊN BẨN VỈA HÈ HÀ NỘI",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Sizzling skewer visual row
    Row(
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      FoodSkewerGraphic(foodId = com.example.data.model.FoodId.CA_VIEN_CHIEN)
      Spacer(modifier = Modifier.width(12.dp))
      FoodSkewerGraphic(foodId = com.example.data.model.FoodId.XUC_XICH_RAN)
      Spacer(modifier = Modifier.width(12.dp))
      FoodSkewerGraphic(foodId = com.example.data.model.FoodId.LAP_XUONG_NUONG_DA)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Career Quick Profile Card
    Card(
      colors = CardDefaults.cardColors(containerColor = DarkAlley),
      shape = RoundedCornerShape(12.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF37474F)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Hồ sơ: ${profile.playerName}",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          horizontalArrangement = Arrangement.SpaceEvenly,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Ngày", fontSize = 10.sp, color = TextSecondary)
            Text(text = "${profile.day}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = SkewerOrange)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Cấp độ", fontSize = 10.sp, color = TextSecondary)
            Text(text = "Cấp ${profile.level}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldenCrisp)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Tiền vốn", fontSize = 10.sp, color = TextSecondary)
            Text(text = "%,d đ".format(profile.cash), fontSize = 14.sp, fontWeight = FontWeight.Black, color = CashGold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Main Actions
    Button(
      onClick = onStartGame,
      colors = ButtonDefaults.buttonColors(containerColor = SizzlingRed),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .testTag("menu_start_game_btn")
    ) {
      Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = if (profile.day > 1) "TIẾP TỤC CA NGÀY ${profile.day}" else "BẮT ĐẦU BÁN HÀNG!",
        fontWeight = FontWeight.Black,
        fontSize = 15.sp,
        color = Color.White
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedButton(
      onClick = { showGuideDialog = true },
      colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(46.dp)
        .testTag("menu_guide_btn")
    ) {
      Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(text = "HƯỚNG DẪN CHƠI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedButton(
      onClick = { showStatsDialog = true },
      colors = ButtonDefaults.outlinedButtonColors(contentColor = CashGold),
      shape = RoundedCornerShape(10.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(46.dp)
        .testTag("menu_stats_btn")
    ) {
      Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(text = "THÀNH TÍCH & KỶ LỤC", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }

    Spacer(modifier = Modifier.height(10.dp))

    TextButton(
      onClick = { showResetConfirm = true },
      modifier = Modifier.testTag("menu_reset_btn")
    ) {
      Icon(Icons.Default.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(4.dp))
      Text(text = "Chơi lại từ đầu (Xóa tiến trình)", fontSize = 11.sp, color = TextMuted)
    }

    Spacer(modifier = Modifier.height(20.dp))
  }

  // Guide Dialog
  if (showGuideDialog) {
    AlertDialog(
      onDismissRequest = { showGuideDialog = false },
      title = {
        Text(text = "📖 CẨM NANG XIÊN TÔI ĐÊ!", fontWeight = FontWeight.Black, color = SkewerOrange)
      },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(
            text = "1. Nhận đơn & Nấu nướng:\n• Thả xiên vào chảo chiên hoặc bếp sỏi đá.\n• Chờ chín vàng gắp ra đĩa ngay (để lâu sẽ bị cháy đen!).\n• Rưới sốt (Tương ớt, sốt me, tương cà) rồi nhấn GIAO trước khi khách hết kiên nhẫn.",
            fontSize = 11.sp,
            color = TextPrimary,
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "2. Cân bằng Lợi Nhuận & Risk Meter:\n• Bày nhiều bàn ghế và bật loa to giúp đông khách nhưng khiến đô thị dòm ngó!\n• Thường xuyên bấm 'Quét rác' để tránh bị tích tụ rác và vệt dầu làm tăng Risk.\n• Khi có nhiều Shipper bu quanh, giao đồ nhanh cho họ để giải tỏa vỉa hè.",
            fontSize = 11.sp,
            color = WarningYellow,
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "3. 'Công an tới!':\n• Có 10 giây thu dọn bàn ghế và cất bếp thật nhanh.\n• Sau khi thu xong, nhấn 'ĐẨY XE TẨU THOÁT' để vào mini-game luồn lách qua ngõ nhỏ Hà Nội!",
            fontSize = 11.sp,
            color = MoneyGreen,
            lineHeight = 16.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showGuideDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = SizzlingRed)
        ) {
          Text(text = "ĐÃ HIỂU!")
        }
      },
      containerColor = DarkAlley
    )
  }

  // Stats Dialog
  if (showStatsDialog) {
    AlertDialog(
      onDismissRequest = { showStatsDialog = false },
      title = {
        Text(text = "🏆 THÀNH TÍCH SỰ NGHIỆP", fontWeight = FontWeight.Black, color = CashGold)
      },
      text = {
        Column {
          StatRow(label = "Tổng số xiên đã bán:", value = "${profile.totalSkewersSold} xiên")
          StatRow(label = "Tổng doanh thu tích lũy:", value = "%,d đ".format(profile.totalRevenueAllTime))
          StatRow(label = "Số lần tẩu thoát đô thị thành công:", value = "${profile.timesEscapedPolice} lần")
          StatRow(label = "Số lần bị lập biên bản phạt:", value = "${profile.timesCaughtByPolice} lần")
          StatRow(label = "Kỷ lục chạy trốn trong ngõ:", value = "${profile.highestGetawayDistance}m")
        }
      },
      confirmButton = {
        Button(
          onClick = { showStatsDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = CashGold)
        ) {
          Text(text = "ĐÓNG", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      containerColor = DarkAlley
    )
  }

  // Reset Dialog
  if (showResetConfirm) {
    AlertDialog(
      onDismissRequest = { showResetConfirm = false },
      title = { Text(text = "Xác nhận chơi lại từ đầu?", color = PoliceRed) },
      text = {
        Text(text = "Toàn bộ tiền bạc, cấp độ và công thức đã mở khóa sẽ được đặt lại về Ngày 1. Bạn chắc chắn chứ?")
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirm = false
            onResetGame()
          },
          colors = ButtonDefaults.buttonColors(containerColor = PoliceRed)
        ) {
          Text(text = "XÓA & CHƠI LẠI")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirm = false }) {
          Text(text = "HỦY")
        }
      },
      containerColor = DarkAlley
    )
  }
}

@Composable
private fun StatRow(label: String, value: String) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    Text(text = label, fontSize = 11.sp, color = TextSecondary)
    Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CashGold)
  }
}
