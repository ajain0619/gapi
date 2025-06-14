package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TrafficSplitTypeTest {

  @Test
  void testType_returnEvaluateType() {
    assertEquals(
        TrafficSplitType.EVALUATE,
        TrafficSplitType.EVALUATE.fromInt(0),
        "Invalid TrafficSplitType");
  }

  @Test
  void testType_returnCoinTossType() {
    assertEquals(
        TrafficSplitType.SSP_COIN_TOSS,
        TrafficSplitType.SSP_COIN_TOSS.fromInt(1),
        "Invalid TrafficSplitType");
  }

  @Test
  void test_outOfBoundsTrafficSplitTypeReturnsDefault() {
    assertEquals(
        TrafficSplitType.EVALUATE,
        TrafficSplitType.SSP_COIN_TOSS.fromInt(200),
        "Should return default TrafficSplitType");
  }

  @Test
  void testType_asIntreturnEvaluateType() {
    assertEquals(0, TrafficSplitType.EVALUATE.asInt(), "Invalid TrafficSplitType");
  }

  @Test
  void testType_asIntreturnCoinTossType() {
    assertEquals(1, TrafficSplitType.SSP_COIN_TOSS.asInt(), "Invalid TrafficSplitType");
  }
}
