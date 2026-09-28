package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.theme.CashGold
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.DeepNight
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.MoneyGreen
import com.example.ui.theme.PlasticRedChair
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SidewalkTile
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningYellow
import kotlinx.coroutines.delay
import kotlin.random.Random

data class AlleyObstacle(
  val id: Long,
  val lane: Int, // 0 = Left, 1 = Center, 2 = Right
  var yPosition: Float, // 0f (top) to 1f (bottom)
  val type: ObstacleType
)

enum class ObstacleType(val emoji: String, val label: String, val isPickup: Boolean = false) {
  MOTORBIKE("🛵", "Xe Lead", false),
  POTHOLE("🕳️", "Ổ gà", false),
  BARRICADE("🚧", "Rào chắn", false),
  STRAY_DOG("🐕", "Cún con", false),
  BONUS_COIN("💵", "Tiền rơi", true),
  BONUS_SKEWER("🍢", "Xiên rơi", true)
}

/**
 * Vertical Arcade Mini-Game: Đẩy xe xiên chạy trốn trong ngõ nhỏ Hà Nội!
 */
@Composable
fun GetawayMiniGameScreen(
  soundManager: SoundManager,
  hasTurboCart: Boolean,
  onFinishMiniGame: (escapedSuccess: Boolean, damageDeduction: Int, bonusCoins: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val maxHealth = if (hasTurboCart) 4 else 3
  var currentHealth by remember { mutableIntStateOf(maxHealth) }
  var distanceRemainingMeters by remember { mutableFloatStateOf(150f) }
  var playerLane by remember { mutableIntStateOf(1) } // 0: Left, 1: Center, 2: Right
  var bonusCoinsCollected by remember { mutableIntStateOf(0) }
  var damageCost by remember { mutableIntStateOf(0) }
  var isGameOver by remember { mutableStateOf(false) }
  var isSuccess by remember { mutableStateOf(false) }

  val obstacles = remember { mutableStateListOf<AlleyObstacle>() }
  var roadScrollY by remember { mutableFloatStateOf(0f) }
  var screenShake by remember { mutableFloatStateOf(0f) }

  // Game loop ticker
  LaunchedEffect(isGameOver) {
    if (isGameOver) return@LaunchedEffect

    var lastSpawnTime = 0L
    var frameId = 0L

    while (distanceRemainingMeters > 0 && currentHealth > 0) {
      delay(33) // ~30 FPS loop for physics & game update
      roadScrollY = (roadScrollY + 0.05f) % 1f
      distanceRemainingMeters = (distanceRemainingMeters - 1.8f).coerceAtLeast(0f)

      if (screenShake > 0) screenShake = (screenShake - 1f).coerceAtLeast(0f)

      // Move obstacles down
      val iterator = obstacles.iterator()
      while (iterator.hasNext()) {
        val obs = iterator.next()
        obs.yPosition += 0.04f

        // Check collision at player position (y ~ 0.78f to 0.88f)
        if (obs.yPosition in 0.75f..0.88f && obs.lane == playerLane) {
          if (obs.type.isPickup) {
            // Picked up bonus!
            bonusCoinsCollected += 15_000
            soundManager.playCoin()
            iterator.remove()
          } else {
            // Crash into obstacle!
            currentHealth--
            damageCost += 20_000
            screenShake = 12f
            soundManager.playCrash()
            iterator.remove()
          }
        } else if (obs.yPosition > 1.1f) {
          iterator.remove()
        }
      }

      // Spawn new obstacles
      val currentTime = System.currentTimeMillis()
      if (currentTime - lastSpawnTime > 800) {
        lastSpawnTime = currentTime
        val randomLane = Random.nextInt(3)
        val isBonus = Random.nextFloat() < 0.25f
        val obsType = if (isBonus) {
          if (Random.nextBoolean()) ObstacleType.BONUS_COIN else ObstacleType.BONUS_SKEWER
        } else {
          listOf(ObstacleType.MOTORBIKE, ObstacleType.POTHOLE, ObstacleType.BARRICADE, ObstacleType.STRAY_DOG).random()
        }
        obstacles.add(
          AlleyObstacle(
            id = frameId++,
            lane = randomLane,
            yPosition = -0.1f,
            type = obsType
          )
        )
      }
    }

    isGameOver = true
    if (distanceRemainingMeters <= 0f) {
      isSuccess = true
      soundManager.playFanfare()
    } else {
      isSuccess = false
      soundManager.playBurn()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DeepNight)
      .pointerInput(Unit) {
        detectHorizontalDragGestures { _, dragAmount ->
          if (!isGameOver) {
            if (dragAmount < -25 && playerLane > 0) {
              playerLane--
              soundManager.playClick()
            } else if (dragAmount > 25 && playerLane < 2) {
              playerLane++
              soundManager.playClick()
            }
          }
        }
      }
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Top HUD: Distance remaining & Cart Durability (Hearts)
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = "TẨU THOÁT TRONG NGÕ!",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = SkewerOrange
          )
          Text(
            text = "Đến ngõ an toàn: ${distanceRemainingMeters.toInt()}m",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = WarningYellow
          )
        }

        // Hearts
        Row(verticalAlignment = Alignment.CenterVertically) {
          for (i in 1..maxHealth) {
            Text(
              text = if (i <= currentHealth) "❤️" else "🖤",
              fontSize = 16.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Distance Progress Bar
      LinearProgressIndicator(
        progress = { (1f - (distanceRemainingMeters / 150f)).coerceIn(0f, 1f) },
        color = MoneyGreen,
        trackColor = Color(0xFF263238),
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Arcade Playfield Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkAlley)
          .border(2.dp, Color(0xFF37474F), RoundedCornerShape(12.dp))
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val laneWidth = w / 3f

          // Draw Hanoi Old Alleyway: brick borders, lane markings
          drawRect(
            color = Color(0xFF212126),
            topLeft = Offset(0f, 0f),
            size = Size(w, h)
          )

          // Alley walls / sidewalk curbs
          drawRect(
            color = SidewalkTile,
            topLeft = Offset(0f, 0f),
            size = Size(16f, h)
          )
          drawRect(
            color = SidewalkTile,
            topLeft = Offset(w - 16f, 0f),
            size = Size(16f, h)
          )

          // Lane dividing dashed lines
          for (laneIdx in 1..2) {
            val lx = laneWidth * laneIdx
            var dashY = (roadScrollY * 40f) % 40f
            while (dashY < h) {
              drawLine(
                color = Color(0x44FFFFFF),
                start = Offset(lx, dashY),
                end = Offset(lx, dashY + 20f),
                strokeWidth = 3f
              )
              dashY += 40f
            }
          }

          // Draw Obstacles & Pickups
          obstacles.forEach { obs ->
            val obsX = laneWidth * obs.lane + (laneWidth / 2f)
            val obsY = obs.yPosition * h

            if (obs.type.isPickup) {
              // Glowing aura for bonus pickups
              drawCircle(
                color = if (obs.type == ObstacleType.BONUS_COIN) CashGold.copy(alpha = 0.4f) else SkewerOrange.copy(alpha = 0.4f),
                radius = 22f,
                center = Offset(obsX, obsY)
              )
            } else {
              // Danger shadow for road obstacle
              drawOval(
                color = Color(0x66000000),
                topLeft = Offset(obsX - 22f, obsY + 12f),
                size = Size(44f, 16f)
              )
            }
          }

          // Draw Player Street Cart
          val playerX = laneWidth * playerLane + (laneWidth / 2f)
          val playerY = h * 0.82f + (if (screenShake > 0) Random.nextFloat() * screenShake - screenShake / 2 else 0f)

          // Cart shadow
          drawOval(
            color = Color(0x88000000),
            topLeft = Offset(playerX - 28f, playerY + 22f),
            size = Size(56f, 18f)
          )

          // Cart Body (Red/Yellow street cart with skewers)
          drawRoundRect(
            color = SizzlingRed,
            topLeft = Offset(playerX - 24f, playerY - 26f),
            size = Size(48f, 44f),
            cornerRadius = CornerRadius(6f, 6f)
          )
          // Cart Roof Canopy
          drawRoundRect(
            color = GoldenCrisp,
            topLeft = Offset(playerX - 26f, playerY - 34f),
            size = Size(52f, 12f),
            cornerRadius = CornerRadius(3f, 3f)
          )
          // Wheels
          drawCircle(Color.Black, radius = 7f, center = Offset(playerX - 20f, playerY + 16f))
          drawCircle(Color.Black, radius = 7f, center = Offset(playerX + 20f, playerY + 16f))
          drawCircle(Color(0xFFB0BEC5), radius = 3f, center = Offset(playerX - 20f, playerY + 16f))
          drawCircle(Color(0xFFB0BEC5), radius = 3f, center = Offset(playerX + 20f, playerY + 16f))
        }

        // Overlay emojis directly onto Canvas positions for crisp rendering
        obstacles.forEach { obs ->
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(
                start = (obs.lane * (330 / 3) + 24).dp,
                top = (obs.yPosition * 400).coerceAtLeast(0f).dp
              )
          ) {
            Text(text = obs.type.emoji, fontSize = 24.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Touch Arrow Controls for Mobile Accessibility
      Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Button(
          onClick = {
            if (!isGameOver && playerLane > 0) {
              playerLane--
              soundManager.playClick()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SizzlingRed),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("steer_left_btn")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trái")
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "RẼ TRÁI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center Indicator
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkAlley)
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = "LÀN: ${if (playerLane == 0) "TRÁI" else if (playerLane == 1) "GIỮA" else "PHẢI"}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CashGold
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
          onClick = {
            if (!isGameOver && playerLane < 2) {
              playerLane++
              soundManager.playClick()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = SizzlingRed),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("steer_right_btn")
        ) {
          Text(text = "RẼ PHẢI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Phải")
        }
      }

      // Game Over / Win Dialog Overlay
      if (isGameOver) {
        Spacer(modifier = Modifier.height(10.dp))
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSuccess) Color(0xFF1B5E20) else Color(0xFFB71C1C))
            .padding(14.dp)
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (isSuccess) "🎉 TẨU THOÁT THÀNH CÔNG!" else "💥 XE VA QUẸT HỎNG HÓC!",
              fontWeight = FontWeight.Black,
              fontSize = 15.sp,
              color = Color.White
            )
            Text(
              text = if (isSuccess)
                "Bạn đã luồn lách qua ngõ nhỏ an toàn, giữ trọn vẹn toàn bộ xe hàng và doanh thu!"
              else
                "Xe bị va quẹt hư hại (-%,d đ). Nhưng may mắn là bạn không bị tạm giữ đồ đạc!".format(damageCost),
              fontSize = 11.sp,
              color = TextPrimary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              modifier = Modifier.padding(vertical = 4.dp)
            )

            if (bonusCoinsCollected > 0) {
              Text(
                text = "Nhặt được thêm: +%,d đ trên đường!".format(bonusCoinsCollected),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CashGold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = {
                onFinishMiniGame(isSuccess, damageCost, bonusCoinsCollected)
              },
              colors = ButtonDefaults.buttonColors(containerColor = CashGold),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("finish_getaway_btn")
            ) {
              Text(
                text = "XEM TỔNG KẾT CA BÁN",
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }
}
