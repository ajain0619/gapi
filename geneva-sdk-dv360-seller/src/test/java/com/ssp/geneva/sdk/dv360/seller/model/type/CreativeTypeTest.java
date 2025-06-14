package com.ssp.geneva.sdk.dv360.seller.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CreativeTypeTest {
  @Test
  void shouldReturnTrueForValidFormat() {
    assertTrue(CreativeType.valid("DISPLAY"));
    assertTrue(CreativeType.valid("display"));
    assertTrue(CreativeType.valid("VIDEO"));
    assertTrue(CreativeType.valid("AUDIO"));
  }

  @Test
  void shouldReturnFalseForInValidFormat() {
    assertFalse(CreativeType.valid("TEST"));
    assertFalse(CreativeType.valid("100"));
    assertFalse(CreativeType.valid(""));
  }

  @ParameterizedTest
  @CsvSource({
    "'VIDEO', CREATIVE_TYPE_VIDEO",
    "'video', CREATIVE_TYPE_VIDEO",
    "'vIdEo', CREATIVE_TYPE_VIDEO",
    "'DISPLAY', CREATIVE_TYPE_DISPLAY"
  })
  void shouldReturnCreativeType(String shortName, CreativeType creativeType) {
    assertEquals(Optional.of(creativeType), CreativeType.fromShortName(shortName));
  }

  @ParameterizedTest
  @CsvSource({"'VIDEO', CREATIVE_TYPE_VIDEO", "'DISPLAY', CREATIVE_TYPE_DISPLAY"})
  void shouldReturnStringShortNameWhenProvidedCreativeType(
      String shortName, CreativeType creativeType) {
    assertTrue(creativeType.hasShortName(shortName));
  }

  @Test
  void shouldReturnNullWhenGivenNullString() {
    assertEquals(Optional.empty(), CreativeType.fromShortName(null));
  }

  @Test
  void shouldReturnNullWhenGivenEmptyString() {
    assertEquals(Optional.empty(), CreativeType.fromShortName(""));
  }
}
