package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LocationTypeTest {

  @ParameterizedTest
  @MethodSource("getLocationTypeArgs")
  void shouldReturnExpectedAdTypeName(String locationTypeName, LocationType locationType) {
    assertEquals(locationTypeName, locationType.getName());
  }

  private static Stream<Arguments> getLocationTypeArgs() {
    return Stream.of(
        Arguments.of("Indoor", LocationType.INDOOR),
        Arguments.of("Outdoor", LocationType.OUTDOOR),
        Arguments.of("Moving", LocationType.MOVING));
  }
}
