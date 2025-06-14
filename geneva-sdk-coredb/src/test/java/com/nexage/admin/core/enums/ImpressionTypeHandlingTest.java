package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class ImpressionTypeHandlingTest {

  @Test
  void testValidActions() {
    assertEquals(
        ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG,
        ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG.fromInt(0));
    assertEquals(
        ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST,
        ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST.fromInt(1));
  }

  @Test
  void testInvalidActions() {
    assertNotEquals(
        ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG,
        ImpressionTypeHandling.BASED_ON_PLACEMENT_CONFIG.fromInt(1));
    assertNotEquals(
        ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST,
        ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST.fromInt(0));
  }
}
