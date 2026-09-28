package com.example

import com.example.data.model.CustomerType
import com.example.data.model.FoodId
import com.example.data.model.LocationId
import com.example.data.model.SauceType
import com.example.data.model.SpeakerLevel
import com.example.data.model.UpgradeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testFoodEconomics_marginIsPositive() {
    FoodId.values().forEach { food ->
      assertTrue("${food.displayName} must have positive profit margin", food.price > food.cost)
      assertTrue("${food.displayName} cooking time must be positive", food.cookTimeSec > 0f)
      assertTrue("${food.displayName} burn time must be greater than cook time", food.burnTimeSec > food.cookTimeSec)
    }
  }

  @Test
  fun testLocationsProgression() {
    val locations = LocationId.values()
    assertTrue(locations.isNotEmpty())
    assertEquals(LocationId.CONG_TRUONG, locations[0])
    assertTrue("Tourist street has highest patrol rate", LocationId.PHO_DU_LICH.basePatrolRate > LocationId.CONG_TRUONG.basePatrolRate)
  }

  @Test
  fun testCustomerPatience() {
    CustomerType.values().forEach { cust ->
      assertTrue("${cust.displayName} patience must be positive", cust.basePatienceSec > 0f)
    }
    // High school students are less patient than tourists
    assertTrue(CustomerType.HOC_SINH.basePatienceSec < CustomerType.DU_KHACH.basePatienceSec)
  }

  @Test
  fun testUpgradesAndSpeaker() {
    UpgradeId.values().forEach { upg ->
      assertTrue("Upgrade cost must be reasonable", upg.cost > 0)
    }
    assertTrue("High speaker volume boosts crowd more than off", SpeakerLevel.HIGH.crowdBoost > SpeakerLevel.OFF.crowdBoost)
    assertTrue("High speaker volume increases risk more than off", SpeakerLevel.HIGH.riskBoost > SpeakerLevel.OFF.riskBoost)
  }
}

