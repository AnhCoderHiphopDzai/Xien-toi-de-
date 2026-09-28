package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CookingStatus
import com.example.data.model.CustomerType
import com.example.data.model.FoodId
import com.example.ui.theme.CashGold
import com.example.ui.theme.ChiliRed
import com.example.ui.theme.DarkAlley
import com.example.ui.theme.GoldenCrisp
import com.example.ui.theme.PlasticBlueTable
import com.example.ui.theme.PlasticRedChair
import com.example.ui.theme.PoliceBlue
import com.example.ui.theme.PoliceRed
import com.example.ui.theme.SidewalkTile
import com.example.ui.theme.SizzlingRed
import com.example.ui.theme.SkewerOrange
import com.example.ui.theme.SkewerYellow
import com.example.ui.theme.WarningYellow
import kotlin.random.Random

/**
 * Pixel-Art Styled Canvas Renderer for Vietnamese Skewers & Street Food Items
 */
@Composable
fun FoodSkewerGraphic(
  foodId: FoodId,
  status: CookingStatus = CookingStatus.DONE,
  modifier: Modifier = Modifier.size(54.dp)
) {
  val transition = rememberInfiniteTransition(label = "sizzle")
  val shimmer by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
    label = "shimmer"
  )

  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Bamboo stick
    val stickColor = Color(0xFFD7CCC8)
    val stickDark = Color(0xFFA1887F)
    drawLine(
      color = stickDark,
      start = Offset(w * 0.15f, h * 0.85f),
      end = Offset(w * 0.85f, h * 0.15f),
      strokeWidth = 5f
    )
    drawLine(
      color = stickColor,
      start = Offset(w * 0.17f, h * 0.83f),
      end = Offset(w * 0.83f, h * 0.17f),
      strokeWidth = 3f
    )

    when (foodId) {
      FoodId.CA_VIEN_CHIEN -> drawFishballs(w, h, status, shimmer)
      FoodId.XUC_XICH_RAN -> drawSausage(w, h, status, shimmer)
      FoodId.NEM_CHUA_RAN -> drawNemChua(w, h, status, shimmer)
      FoodId.LAP_XUONG_NUONG_DA -> drawLapXuong(w, h, status, shimmer)
      FoodId.KHOAI_LOC_XOAY -> drawPotatoTornado(w, h, status, shimmer)
      FoodId.TRA_CHANH -> drawLemonTeaCup(w, h, status, shimmer)
    }

    if (status == CookingStatus.COOKING) {
      // Draw rising sizzle bubbles
      val bubbleY = h * 0.35f - (shimmer * 16f)
      drawCircle(Color(0xCCFFF176), radius = 3.5f, center = Offset(w * 0.5f, bubbleY))
      drawCircle(Color(0x88FFEB3B), radius = 2f, center = Offset(w * 0.65f, bubbleY + 6f))
    }
  }
}

private fun DrawScope.drawFishballs(w: Float, h: Float, status: CookingStatus, shimmer: Float) {
  val baseColor = when (status) {
    CookingStatus.RAW -> Color(0xFFEEEEEE)
    CookingStatus.COOKING -> Color(0xFFFFD54F)
    CookingStatus.DONE -> Color(0xFFFFB300)
    CookingStatus.BURNT -> Color(0xFF3E2723)
  }
  val highlightColor = when (status) {
    CookingStatus.RAW -> Color(0xFFFFFFFF)
    CookingStatus.COOKING -> Color(0xFFFFF9C4)
    CookingStatus.DONE -> Color(0xFFFFE082)
    CookingStatus.BURNT -> Color(0xFF5D4037)
  }

  val centers = listOf(
    Offset(w * 0.35f, h * 0.65f),
    Offset(w * 0.5f, h * 0.5f),
    Offset(w * 0.65f, h * 0.35f)
  )

  centers.forEachIndexed { idx, center ->
    drawCircle(baseColor, radius = w * 0.14f, center = center)
    drawCircle(highlightColor, radius = w * 0.06f, center = center - Offset(3f, 3f))
    if (status == CookingStatus.DONE) {
      // Crispy fried speckles
      drawCircle(Color(0xFFE65100), radius = 2f, center = center + Offset(4f, 2f))
      drawCircle(Color(0xFFBF360C), radius = 1.5f, center = center - Offset(2f, 4f))
    }
  }
}

private fun DrawScope.drawSausage(w: Float, h: Float, status: CookingStatus, shimmer: Float) {
  val bodyColor = when (status) {
    CookingStatus.RAW -> Color(0xFFEF9A9A)
    CookingStatus.COOKING -> Color(0xFFE53935)
    CookingStatus.DONE -> Color(0xFFC62828)
    CookingStatus.BURNT -> Color(0xFF261C1A)
  }

  // Sausage diagonal capsule
  drawRoundRect(
    color = bodyColor,
    topLeft = Offset(w * 0.28f, h * 0.28f),
    size = Size(w * 0.44f, h * 0.44f),
    cornerRadius = CornerRadius(w * 0.15f, h * 0.15f)
  )

  // Grilling cross-cut marks
  val cutColor = if (status == CookingStatus.BURNT) Color.Black else Color(0xFFB71C1C)
  drawLine(cutColor, Offset(w * 0.35f, h * 0.42f), Offset(w * 0.48f, h * 0.35f), strokeWidth = 3f)
  drawLine(cutColor, Offset(w * 0.44f, h * 0.54f), Offset(w * 0.58f, h * 0.46f), strokeWidth = 3f)
  drawLine(cutColor, Offset(w * 0.54f, h * 0.65f), Offset(w * 0.66f, h * 0.58f), strokeWidth = 3f)
}

private fun DrawScope.drawNemChua(w: Float, h: Float, status: CookingStatus, shimmer: Float) {
  val crumbColor = when (status) {
    CookingStatus.RAW -> Color(0xFFFFCC80)
    CookingStatus.COOKING -> Color(0xFFFFB74D)
    CookingStatus.DONE -> Color(0xFFF57C00)
    CookingStatus.BURNT -> Color(0xFF424242)
  }

  drawRoundRect(
    color = crumbColor,
    topLeft = Offset(w * 0.3f, h * 0.25f),
    size = Size(w * 0.4f, h * 0.5f),
    cornerRadius = CornerRadius(6f, 6f)
  )
  // Breadcrumb flakes
  drawCircle(Color(0xFFFFE082), radius = 2.5f, center = Offset(w * 0.4f, h * 0.4f))
  drawCircle(Color(0xFFE65100), radius = 2f, center = Offset(w * 0.55f, h * 0.6f))
}

private fun DrawScope.drawLapXuong(w: Float, h: Float, status: CookingStatus, shimmer: Float) {
  val redColor = when (status) {
    CookingStatus.RAW -> Color(0xFFD32F2F)
    CookingStatus.COOKING -> Color(0xFFB71C1C)
    CookingStatus.DONE -> Color(0xFF880E4F)
    CookingStatus.BURNT -> Color(0xFF1F1216)
  }

  // Grilled dark pebble look beneath
  drawCircle(Color(0xFF424242), radius = w * 0.16f, center = Offset(w * 0.5f, h * 0.62f))
  drawCircle(Color(0xFF616161), radius = w * 0.14f, center = Offset(w * 0.36f, h * 0.65f))

  drawRoundRect(
    color = redColor,
    topLeft = Offset(w * 0.32f, h * 0.24f),
    size = Size(w * 0.36f, h * 0.52f),
    cornerRadius = CornerRadius(w * 0.18f, w * 0.18f)
  )
  // Chili pepper flakes on top
  drawCircle(Color(0xFFFF1744), radius = 2.5f, center = Offset(w * 0.45f, h * 0.4f))
  drawCircle(Color(0xFFFFD600), radius = 1.5f, center = Offset(w * 0.52f, h * 0.52f))
  drawCircle(Color(0xFFFF1744), radius = 2f, center = Offset(w * 0.42f, h * 0.6f))
}

private fun DrawScope.drawPotatoTornado(w: Float, h: Float, status: CookingStatus, shimmer: Float) {
  val spiralColor = when (status) {
    CookingStatus.RAW -> Color(0xFFFFF59D)
    CookingStatus.COOKING -> Color(0xFFFFEE58)
    CookingStatus.DONE -> Color(0xFFFFA000)
    CookingStatus.BURNT -> Color(0xFF4E342E)
  }

  for (i in 0..4) {
    val y = h * (0.28f + i * 0.11f)
    drawRoundRect(
      color = spiralColor,
      topLeft = Offset(w * 0.22f, y),
      size = Size(w * 0.56f, h * 0.08f),
      cornerRadius = CornerRadius(4f, 4f)
    )
  }
}

private fun DrawScope.drawLemonTeaCup(w: Float, h: Float, status: CookingStatus, shimmer: Float) {
  // Clear plastic cup
  val path = Path().apply {
    moveTo(w * 0.3f, h * 0.25f)
    lineTo(w * 0.7f, h * 0.25f)
    lineTo(w * 0.64f, h * 0.82f)
    lineTo(w * 0.36f, h * 0.82f)
    close()
  }
  // Amber tea liquid
  drawPath(path, Brush.verticalGradient(listOf(Color(0xFFFFF176), Color(0xFFFFB300))))
  drawPath(path, Color(0xFF37474F), style = Stroke(width = 3f))

  // Straw
  drawLine(
    color = Color(0xFF00E676),
    start = Offset(w * 0.52f, h * 0.15f),
    end = Offset(w * 0.42f, h * 0.75f),
    strokeWidth = 4f
  )
  // Ice cube
  drawRect(Color(0xCCFFFFFF), topLeft = Offset(w * 0.45f, h * 0.45f), size = Size(w * 0.14f, h * 0.14f))
  // Lime slice
  drawCircle(Color(0xFF76FF03), radius = w * 0.09f, center = Offset(w * 0.56f, h * 0.35f))
}

/**
 * Plastic Hanoi Street Furniture Graphic: Red Stool & Blue Table
 */
@Composable
fun HanoiStreetFurnitureGraphic(
  tableCount: Int,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Sidewalk pavement tile background line
    drawRect(
      color = SidewalkTile,
      topLeft = Offset(0f, h * 0.7f),
      size = Size(w, h * 0.3f)
    )
    for (x in 0..(w.toInt()) step 24) {
      drawLine(
        color = Color(0x33000000),
        start = Offset(x.toFloat(), h * 0.7f),
        end = Offset(x.toFloat(), h),
        strokeWidth = 2f
      )
    }

    if (tableCount > 0) {
      val spacing = w / (tableCount + 1)
      for (i in 1..tableCount) {
        val cx = spacing * i
        // Blue Plastic Table
        drawRoundRect(
          color = PlasticBlueTable,
          topLeft = Offset(cx - 18.dp.toPx(), h * 0.45f),
          size = Size(36.dp.toPx(), 18.dp.toPx()),
          cornerRadius = CornerRadius(4f, 4f)
        )
        // Table legs
        drawLine(
          color = Color(0xFF1565C0),
          start = Offset(cx - 14.dp.toPx(), h * 0.54f),
          end = Offset(cx - 16.dp.toPx(), h * 0.75f),
          strokeWidth = 3f
        )
        drawLine(
          color = Color(0xFF1565C0),
          start = Offset(cx + 14.dp.toPx(), h * 0.54f),
          end = Offset(cx + 16.dp.toPx(), h * 0.75f),
          strokeWidth = 3f
        )

        // Red Plastic Stools next to table
        drawRoundRect(
          color = PlasticRedChair,
          topLeft = Offset(cx - 30.dp.toPx(), h * 0.58f),
          size = Size(11.dp.toPx(), 12.dp.toPx()),
          cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
          color = PlasticRedChair,
          topLeft = Offset(cx + 19.dp.toPx(), h * 0.58f),
          size = Size(11.dp.toPx(), 12.dp.toPx()),
          cornerRadius = CornerRadius(2f, 2f)
        )
      }
    }
  }
}

/**
 * Pixel Avatar for Customer with Patience Emoji
 */
@Composable
fun CustomerAvatarBox(
  customerType: CustomerType,
  patienceFraction: Float,
  modifier: Modifier = Modifier
) {
  val emotionEmoji = when {
    patienceFraction > 0.6f -> "😋"
    patienceFraction > 0.3f -> "😐"
    patienceFraction > 0.1f -> "😠"
    else -> "💢"
  }

  val ringColor = when {
    patienceFraction > 0.6f -> Color(0xFF00E676)
    patienceFraction > 0.3f -> Color(0xFFFFD600)
    else -> Color(0xFFFF1744)
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(46.dp)
      .clip(CircleShape)
      .background(DarkAlley)
      .border(2.5.dp, ringColor, CircleShape)
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = customerType.avatar, fontSize = 18.sp)
      Text(text = emotionEmoji, fontSize = 10.sp)
    }
  }
}

/**
 * Police Flasher Strobe Light Banner
 */
@Composable
fun PoliceWarningBanner(
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "police_strobe")
  val flash by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(250, easing = LinearEasing), RepeatMode.Reverse),
    label = "flash"
  )

  val leftColor = if (flash > 0.5f) PoliceBlue else Color(0xFF0D47A1)
  val rightColor = if (flash <= 0.5f) PoliceRed else Color(0xFFB71C1C)

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(
        Brush.horizontalGradient(
          listOf(leftColor, Color.Black, rightColor)
        )
      )
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Text(text = "🚨", fontSize = 20.sp)
    Spacer(modifier = Modifier.width(8.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "CẢNH BÁO: TRẬT TỰ ĐÔ THỊ ĐANG TIẾP CẬN!",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 12.sp
      )
      Text(
        text = "Risk Meter sắp đầy! Giảm bớt ồn ào và rác thải ngay!",
        color = WarningYellow,
        fontSize = 11.sp
      )
    }
  }
}
