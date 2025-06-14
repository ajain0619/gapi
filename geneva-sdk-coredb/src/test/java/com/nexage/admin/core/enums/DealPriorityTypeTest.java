package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DealPriorityTypeTest {

  @Test
  void shouldCorrectlyReturnPriorityInt() {
    assertEquals(
        Map.of(DealPriorityType.OPEN, 0),
        Arrays.stream(DealPriorityType.values())
            .collect(Collectors.toMap(Function.identity(), DealPriorityType::getPriorityType)));
  }

  @Test
  void shouldCorrectlyMapPriorityIntToDealPriorityType() {
    assertNull(DealPriorityType.fromInt(9999));
    assertEquals(
        Map.of(0, DealPriorityType.OPEN),
        Arrays.stream(DealPriorityType.values())
            .map(DealPriorityType::getPriorityType)
            .collect(Collectors.toMap(Function.identity(), DealPriorityType::fromInt)));
  }
}
