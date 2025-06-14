package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AdTypeTest {

  @ParameterizedTest
  @MethodSource("getAdTypeArgs")
  void shouldReturnExpectedAdTypeName(String adTypeName, AdType adType) {
    assertEquals(adTypeName, adType.getName());
  }

  @ParameterizedTest
  @MethodSource("getAdTypeArgs")
  void shouldMapNameToAdTypeWithoutExcpetion(String adTypeName, AdType expectedAdType) {
    assertEquals(expectedAdType, AdType.getValueFromName(adTypeName));
  }

  private static Stream<Arguments> getAdTypeArgs() {
    return Stream.of(
        Arguments.of("Image", AdType.IMAGE),
        Arguments.of("Silent Video", AdType.SILENT_VIDEO),
        Arguments.of("Sound-on Video", AdType.SOUND_ON_VIDEO));
  }
}
