package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FeeTypeTest {

  @Test
  void testFeeTypePercentage() {
    assertEquals(0, FeeType.PERCENTAGE.asInt(), "Invalid FeeType");
  }
}
