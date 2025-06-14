package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class AdSizeFilterTypeTest {

  @Test
  void test_adSizeFilterValuesWithEnum() {
    Arrays.stream(AdSizeFilter.values())
        .map(AdSizeFilter::asActual)
        .forEach(
            value ->
                assertEquals(
                    value.replace("x", "X"),
                    AdSizeFilter.fromActual(value).toString().replace("_", "")));
  }

  @Test
  void test_nonExistentAdSizeFilterThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> AdSizeFilter.valueOf("12000x12001"));
  }
}
