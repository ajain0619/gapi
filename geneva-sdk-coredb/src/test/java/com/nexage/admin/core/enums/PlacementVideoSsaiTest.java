package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PlacementVideoSsaiTest {
  PlacementVideoSsai ssai = PlacementVideoSsai.UNKNOWN;

  @Test
  void shouldNotExistIfValueIsOutOfRange() {
    assertNull(ssai.fromInt(4));
  }

  @Test
  void shouldExistWithRightValue() {
    assertEquals(PlacementVideoSsai.UNKNOWN, ssai.fromInt(0));
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, ssai.fromInt(1));
    assertEquals(PlacementVideoSsai.ASSETS_STICHED_SERVER_SIDE, ssai.fromInt(2));
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, ssai.fromInt(3));
  }

  @Test
  void shouldReturnCorrectInt() {
    ssai = PlacementVideoSsai.UNKNOWN;
    assertEquals(0, ssai.asInt());

    ssai = PlacementVideoSsai.ALL_CLIENT_SIDE;
    assertEquals(1, ssai.asInt());

    ssai = PlacementVideoSsai.ASSETS_STICHED_SERVER_SIDE;
    assertEquals(2, ssai.asInt());

    ssai = PlacementVideoSsai.ALL_SERVER_SIDE;
    assertEquals(3, ssai.asInt());
  }
}
