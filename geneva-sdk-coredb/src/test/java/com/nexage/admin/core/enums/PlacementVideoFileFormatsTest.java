package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PlacementVideoFileFormatsTest {
  PlacementVideoFileFormats fileFormats = PlacementVideoFileFormats.MPEG4;

  @Test
  void shouldNotExistIfValueIsOutOfRange() {
    assertNull(fileFormats.fromInt(4));
  }

  @Test
  void shouldExistWithRightValue0() {
    assertEquals(PlacementVideoFileFormats.MPEG4, fileFormats.fromInt(0));
  }

  @Test
  void shouldExistWithRightValue1() {
    assertEquals(PlacementVideoFileFormats.HLS, fileFormats.fromInt(1));
  }

  @Test
  void shouldExistWithRightValue2() {
    assertEquals(PlacementVideoFileFormats.WEBM, fileFormats.fromInt(2));
  }

  @Test
  void shouldExistWithRightValue3() {
    assertEquals(PlacementVideoFileFormats.WINDOWS_MEDIA, fileFormats.fromInt(3));
  }

  @Test
  void shouldReturnCorrectInt() {
    fileFormats = PlacementVideoFileFormats.MPEG4;
    assertEquals(0, fileFormats.asInt());

    fileFormats = PlacementVideoFileFormats.HLS;
    assertEquals(1, fileFormats.asInt());

    fileFormats = PlacementVideoFileFormats.WEBM;
    assertEquals(2, fileFormats.asInt());

    fileFormats = PlacementVideoFileFormats.WINDOWS_MEDIA;
    assertEquals(3, fileFormats.asInt());
  }
}
