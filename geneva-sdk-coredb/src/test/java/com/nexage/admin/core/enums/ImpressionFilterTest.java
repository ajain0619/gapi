package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ImpressionFilterTest {
  @Test
  void test_impressionFilterOneValue() {
    assertEquals(ImpressionFilter.BANNER, ImpressionFilter.fromActual("1"));
  }

  @Test
  void test_impressionFilterTwoValue() {
    assertEquals(ImpressionFilter.VIDEO, ImpressionFilter.fromActual("2"));
  }

  @Test
  void test_impressionFilterThreeValue() {
    assertEquals(ImpressionFilter.NATIVE, ImpressionFilter.fromActual("3"));
  }

  @Test
  void test_outOfBoundsImpressionFilterIsNull() {
    assertNull(ImpressionFilter.fromActual("12"));
  }
}
