package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PlacementVideoStreamTypeTest {
  PlacementVideoStreamType streamType = PlacementVideoStreamType.VOD;

  @Test
  void shouldNotExistIfValueIsOutOfRange() {
    assertNull(streamType.fromInt(2));
  }

  @Test
  void shouldExistWithRightValue() {
    assertEquals(PlacementVideoStreamType.VOD, streamType.fromInt(0));
    assertEquals(PlacementVideoStreamType.LIVE, streamType.fromInt(1));
  }

  @Test
  void shouldReturnCorrectInt() {
    streamType = PlacementVideoStreamType.VOD;
    assertEquals(0, streamType.asInt());

    streamType = PlacementVideoStreamType.LIVE;
    assertEquals(1, streamType.asInt());
  }
}
