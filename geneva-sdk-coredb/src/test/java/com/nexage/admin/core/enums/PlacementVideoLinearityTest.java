package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PlacementVideoLinearityTest {
  PlacementVideoLinearity linearity = PlacementVideoLinearity.LINEAR;

  @Test
  void shouldNotExistIfValueIsOutOfRange() {
    assertNull(linearity.fromInt(3));
  }

  @Test
  void shouldExistWithRightValue() {
    assertEquals(PlacementVideoLinearity.LINEAR, linearity.fromInt(1));
    assertEquals(PlacementVideoLinearity.NON_LINEAR, linearity.fromInt(2));
  }

  @Test
  void shouldReturnCorrectInt() {
    linearity = PlacementVideoLinearity.LINEAR;
    assertEquals(1, linearity.asInt());

    linearity = PlacementVideoLinearity.NON_LINEAR;
    assertEquals(2, linearity.asInt());
  }
}
