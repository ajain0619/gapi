package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BearingTest {

  @ParameterizedTest
  @MethodSource("getBearingArgs")
  void shouldReturnExpectedBearingName(String bearingName, Bearing bearing) {
    assertEquals(bearingName, bearing.name());
  }

  private static Stream<Arguments> getBearingArgs() {
    return Stream.of(
        Arguments.of("N", Bearing.N),
        Arguments.of("S", Bearing.S),
        Arguments.of("W", Bearing.W),
        Arguments.of("E", Bearing.E),
        Arguments.of("NE", Bearing.NE),
        Arguments.of("NW", Bearing.NW),
        Arguments.of("SE", Bearing.SE),
        Arguments.of("SW", Bearing.SW));
  }
}
