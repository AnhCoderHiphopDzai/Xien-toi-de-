package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LocationId(
  val displayName: String,
  val subtitle: String,
  val description: String,
  val unlockLevel: Int,
  val basePatrolRate: Float,
  val avgTicketMultiplier: Float
) {
  CONG_TRUONG(
    displayName = "Cổng Trường Cấp 3",
    subtitle = "Giờ tan tầm nhộn nhịp",
    description = "Học sinh đông, thao tác nhanh, đồ giá rẻ, kiên nhẫn thấp. Trật tự đô thị tuần tra mức trung bình.",
    unlockLevel = 1,
    basePatrolRate = 1.0f,
    avgTicketMultiplier = 1.0f
  ),
  KTX_DAI_HOC(
    displayName = "Khu KTX Đại Học",
    subtitle = "Đêm hội sinh viên",
    description = "Sinh viên ăn đêm đông đúc, thích gọi combo và săn đón món hot trend. Doanh thu khá cao.",
    unlockLevel = 2,
    basePatrolRate = 1.35f,
    avgTicketMultiplier = 1.3f
  ),
  PHO_DU_LICH(
    displayName = "Phố Đi Bộ & Phố Cổ",
    subtitle = "Thủ phủ ăn vặt đêm",
    description = "Du khách và người nước ngoài chịu chi, tiền tip khủng! Nhưng trật tự đô thị đi tuần liên tục!",
    unlockLevel = 4,
    basePatrolRate = 1.75f,
    avgTicketMultiplier = 1.7f
  )
}

enum class CustomerType(
  val displayName: String,
  val avatar: String,
  val basePatienceSec: Float,
  val tipChance: Float,
  val isShipper: Boolean = false
) {
  HOC_SINH("Học sinh", "🎒", 18f, 0.4f),
  SINH_VIEN("Sinh viên", "🎧", 22f, 0.6f),
  DU_KHACH("Khách du lịch", "📸", 26f, 0.85f),
  VANG_LAI("Khách vãng lai", "🛵", 20f, 0.5f),
  SHIPPER("Shipper công nghệ", "📦", 24f, 0.3f, isShipper = true)
}

enum class CookingStationType {
  FRYER,
  STONE_GRILL,
  DRINK_STATION
}

enum class FoodId(
  val displayName: String,
  val price: Int,
  val cost: Int,
  val station: CookingStationType,
  val cookTimeSec: Float,
  val burnTimeSec: Float,
  val emoji: String,
  val unlockLevel: Int,
  val description: String
) {
  CA_VIEN_CHIEN(
    "Cá Viên Chiên",
    10_000,
    4_000,
    CookingStationType.FRYER,
    3.0f,
    6.0f,
    "🍢",
    1,
    "Xiên cá viên vàng ruộm, dai ngon chuẩn vị cổng trường"
  ),
  XUC_XICH_RAN(
    "Xúc Xích Rán",
    15_000,
    6_000,
    CookingStationType.FRYER,
    4.0f,
    7.5f,
    "🌭",
    1,
    "Xúc xích khứa hoa nứt vỏ thơm lừng béo ngậy"
  ),
  NEM_CHUA_RAN(
    "Nem Chua Rán",
    14_000,
    5_500,
    CookingStationType.FRYER,
    3.5f,
    6.5f,
    "🥢",
    2,
    "Nem chua tẩm bột chiên xù ròn rụm phố Hàng Bông"
  ),
  LAP_XUONG_NUONG_DA(
    "Lạp Xưởng Nướng Đá",
    22_000,
    9_000,
    CookingStationType.STONE_GRILL,
    5.0f,
    8.5f,
    "🥓",
    3,
    "Lạp xưởng Hà Khẩu xèo xèo trên sỏi nóng rắc bột ớt"
  ),
  KHOAI_LOC_XOAY(
    "Khoai Lốc Xoáy",
    18_000,
    7_000,
    CookingStationType.FRYER,
    4.5f,
    7.5f,
    "🥔",
    3,
    "Khoai tây xoắn ốc giòn tan lắc phô mai bột thơm lừng"
  ),
  TRA_CHANH(
    "Trà Chanh Phố Cổ",
    12_000,
    3_000,
    CookingStationType.DRINK_STATION,
    1.5f,
    999f,
    "🍋",
    2,
    "Trà chanh giã tay mát lạnh chua thanh giải ngấy"
  )
}

enum class CookingStatus {
  RAW,
  COOKING,
  DONE,
  BURNT
}

enum class SauceType(val displayName: String, val emoji: String) {
  TUONG_OT("Tương ớt cay", "🌶️"),
  SOT_ME("Sốt me chua ngọt", "🍯"),
  TUONG_CA("Tương cà", "🍅"),
  NONE("Không sốt", "⚪")
}

enum class UpgradeId(
  val title: String,
  val description: String,
  val cost: Int,
  val levelRequired: Int,
  val iconEmoji: String
) {
  FRYER_TURBO(
    "Bếp Chiên Siêu Tốc",
    "Thanh nhiệt công suất lớn, chiên nhanh hơn 35%",
    80_000,
    1,
    "⚡"
  ),
  COMPACT_STOOLS(
    "Bộ Ghế Xếp Gọn",
    "Giảm 50% mức chú ý của đô thị khi bày bàn ghế",
    100_000,
    2,
    "🪑"
  ),
  BIG_TRASH_BIN(
    "Thùng Rác Inox Đậy Nắp",
    "Giảm 60% tốc độ tích tụ rác và mỡ bẩn",
    75_000,
    2,
    "🗑️"
  ),
  SMART_SPEAKER(
    "Loa Kéo BASS Boost Âm",
    "Hút khách tăng 50% nhưng kiểm soát âm thanh ít bị dòm ngó",
    120_000,
    3,
    "📢"
  ),
  TURBO_CART(
    "Xe Đẩy Khung Hợp Kim",
    "Xe nhẹ lướt êm, tăng 1 máu và dễ né chướng ngại vật trong ngõ",
    150_000,
    3,
    "🛒"
  ),
  COLD_CONTAINER(
    "Thùng Đá Cách Nhiệt 3 Lớp",
    "Rót nước trà chanh siêu nhanh chỉ mất 0.8 giây",
    65_000,
    1,
    "🧊"
  )
}

/** In-game active customer order */
data class CustomerOrder(
  val id: String,
  val customerType: CustomerType,
  val items: List<FoodId>,
  val requiredSauce: SauceType,
  val maxPatienceSec: Float,
  var currentPatienceSec: Float,
  val arrivesAtSec: Float,
  val isServed: Boolean = false,
  val tableName: String? = null // null means takeaway/shipper
) {
  val patienceFraction: Float
    get() = (currentPatienceSec / maxPatienceSec).coerceIn(0f, 1f)

  val totalValue: Int
    get() = items.sumOf { it.price }
}

/** Active food on cooking slot */
data class CookingSlot(
  val slotId: Int,
  val stationType: CookingStationType,
  var foodId: FoodId? = null,
  var status: CookingStatus = CookingStatus.RAW,
  var progressSec: Float = 0f,
  var targetSec: Float = 0f
)

/** Plated finished item ready to be packed and given with sauce */
data class PlatedItem(
  val id: String,
  val foodId: FoodId,
  var sauce: SauceType = SauceType.NONE
)

/** Trending food record */
data class FoodTrend(
  val foodId: FoodId,
  val remainingDays: Int,
  val priceBonusPercent: Int = 50,
  val orderWeightBonus: Float = 2.5f
)

/** Shift setup parameters */
data class ShiftSetup(
  val locationId: LocationId = LocationId.CONG_TRUONG,
  val tableCount: Int = 2, // 0 to 5 sets of plastic tables/stools
  val speakerVolume: SpeakerLevel = SpeakerLevel.LOW,
  val hasPriceSign: Boolean = true,
  val hasIceBox: Boolean = true
)

enum class SpeakerLevel(val label: String, val crowdBoost: Float, val riskBoost: Float) {
  OFF("Tắt loa", 1.0f, 0f),
  LOW("Nhạc nhẹ du dương", 1.25f, 0.12f),
  HIGH("Bật căng âm lượng!", 1.65f, 0.35f)
}

/** Shift Performance Summary */
data class ShiftReport(
  val day: Int,
  val location: LocationId,
  val customersServed: Int,
  val customersLost: Int,
  val totalRevenue: Int,
  val totalTips: Int,
  val ingredientCost: Int,
  val policeFine: Int,
  val damageCost: Int,
  val netProfit: Int,
  val xpEarned: Int,
  val escapedPolice: Boolean,
  val wasRaidEncountered: Boolean,
  val peakRiskPercent: Int
)

/** Persistent Player Profile in Room */
@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
  @PrimaryKey val id: Int = 1,
  val playerName: String = "Anh Ba Quê",
  val cash: Int = 120_000, // starting capital in VND
  val day: Int = 1,
  val level: Int = 1,
  val xp: Int = 0,
  val currentTrendFood: String = FoodId.LAP_XUONG_NUONG_DA.name,
  val trendDaysLeft: Int = 3,
  val unlockedFoodsCsv: String = "CA_VIEN_CHIEN,XUC_XICH_RAN,TRA_CHANH",
  val ownedUpgradesCsv: String = "",
  val totalSkewersSold: Int = 0,
  val totalRevenueAllTime: Int = 0,
  val timesEscapedPolice: Int = 0,
  val timesCaughtByPolice: Int = 0,
  val highestGetawayDistance: Int = 0
)
