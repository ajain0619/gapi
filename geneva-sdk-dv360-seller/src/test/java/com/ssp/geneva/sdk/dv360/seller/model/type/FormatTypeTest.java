package com.ssp.geneva.sdk.dv360.seller.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FormatTypeTest {

  @Test
  void shouldReturnTrueForValidFormat() {
    assertTrue(FormatType.valid("DISPLAY"));
    assertTrue(FormatType.valid("display"));
    assertTrue(FormatType.valid("VIDEO"));
    assertTrue(FormatType.valid("AUDIO"));
  }

  @Test
  void shouldReturnFalseForInValidFormat() {
    assertFalse(FormatType.valid("TEST"));
    assertFalse(FormatType.valid("100"));
    assertFalse(FormatType.valid(""));
  }

  @ParameterizedTest
  @CsvSource({
    "'VIDEO', DEAL_FORMAT_VIDEO",
    "'video', DEAL_FORMAT_VIDEO",
    "'vIdEo', DEAL_FORMAT_VIDEO",
    "'DISPLAY', DEAL_FORMAT_DISPLAY"
  })
  void shouldReturnFormatType(String shortName, FormatType formatType) {
    assertEquals(Optional.of(formatType), FormatType.fromShortName(shortName));
  }

  @ParameterizedTest
  @CsvSource({"'VIDEO', DEAL_FORMAT_VIDEO", "'DISPLAY', DEAL_FORMAT_DISPLAY"})
  void shouldReturnStringShortNameWhenProvidedFormatType(String shortName, FormatType formatType) {
    assertTrue(formatType.hasShortName(shortName));
  }

  @Test
  void shouldReturnNullWhenGivenNullString() {
    assertEquals(Optional.empty(), FormatType.fromShortName(null));
  }

  @Test
  void shouldReturnNullWhenGivenEmptyString() {
    assertEquals(Optional.empty(), FormatType.fromShortName(""));
  }
}
