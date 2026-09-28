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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.FoodId
import com.example.data.model.PlayerProfileEntity
import com.example.data.model.UpgradeId
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
 * Chợ Đồ Nghề & Nâng Cấp Quán Xiên
 */
@Composable
fun UpgradesShopScreen(
  profile: PlayerProfileEntity,
  onBuyUpgrade: (UpgradeId) -> Unit,
  onUnlockFoodRecipe: (FoodId) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  val ownedUpgrades = profile.ownedUpgradesCsv.split(",").filter { it.isNotBlank() }.toSet()
  val unlockedFoods = profile.unlockedFoodsCsv.split(",").filter { it.isNotBlank() }.toSet()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNight)
      .verticalScroll(scrollState)
      .padding(16.dp)
  ) {
    // Top Bar with Back Button
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("shop_back_btn")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "CHỢ ĐỒ NGHỀ PHỐ CỔ",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = SkewerOrange
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(DarkAlley)
          .border(1.dp, CashGold, RoundedCornerShape(20.dp))
          .padding(horizontal = 10.dp, vertical = 5.dp)
      ) {
        Text(text = "💵 ", fontSize = 12.sp)
        Text(
          text = "%,d đ".format(profile.cash),
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = CashGold
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Section 1: Trang thiết bị nâng cấp
    Text(
      text = "THIẾT BỊ NÂNG CẤP XE HÀNG",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = CashGold
    )
    Spacer(modifier = Modifier.height(6.dp))

    UpgradeId.values().forEach { upg ->
      val isOwned = ownedUpgrades.contains(upg.name)
      val canAfford = profile.cash >= upg.cost
      val levelUnlocked = profile.level >= upg.levelRequired

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (isOwned) Color(0xFF1E281F) else DarkAlley
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isOwned) MoneyGreen else Color(0xFF37474F)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(CardBgDark)
          ) {
            Text(text = upg.iconEmoji, fontSize = 20.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = upg.title,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = TextPrimary
            )
            Text(
              text = upg.description,
              fontSize = 10.sp,
              color = TextSecondary,
              lineHeight = 14.sp
            )
            if (!levelUnlocked) {
              Text(
                text = "Yêu cầu Cấp ${upg.levelRequired}",
                fontSize = 9.sp,
                color = PoliceRed,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          if (isOwned) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MoneyGreen)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "ĐÃ MUA", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
          } else {
            Button(
              onClick = { onBuyUpgrade(upg) },
              enabled = levelUnlocked && canAfford,
              colors = ButtonDefaults.buttonColors(
                containerColor = SizzlingRed,
                disabledContainerColor = Color(0xFF37474F)
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("buy_upgrade_${upg.name}")
            ) {
              Text(
                text = "%,d đ".format(upg.cost),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Section 2: Công thức món ăn vỉa hè
    Text(
      text = "CÔNG THỨC MÓN ĂN VỈA HÈ MỚI",
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      color = CashGold
    )
    Spacer(modifier = Modifier.height(6.dp))

    FoodId.values().forEach { food ->
      val isUnlocked = unlockedFoods.contains(food.name)
      val unlockCost = food.cost * 6 // formula license cost
      val canAfford = profile.cash >= unlockCost
      val levelUnlocked = profile.level >= food.unlockLevel

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (isUnlocked) Color(0xFF1E281F) else DarkAlley
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isUnlocked) MoneyGreen else Color(0xFF37474F)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = food.emoji, fontSize = 24.sp)

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = food.displayName,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = TextPrimary
            )
            Text(
              text = food.description,
              fontSize = 10.sp,
              color = TextSecondary,
              lineHeight = 13.sp
            )
            Text(
              text = "Giá bán: %,d đ • Lãi: %,d đ/xiên".format(food.price, food.price - food.cost),
              fontSize = 9.sp,
              color = CashGold
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          if (isUnlocked) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MoneyGreen)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(text = "ĐANG BÁN", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
          } else {
            Button(
              onClick = { onUnlockFoodRecipe(food) },
              enabled = levelUnlocked && canAfford,
              colors = ButtonDefaults.buttonColors(
                containerColor = GoldenCrisp,
                disabledContainerColor = Color(0xFF37474F)
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("unlock_recipe_${food.name}")
            ) {
              Text(
                text = "MỞ: %,d đ".format(unlockCost),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
