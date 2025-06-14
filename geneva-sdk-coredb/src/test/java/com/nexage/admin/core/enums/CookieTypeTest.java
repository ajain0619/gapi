package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CookieTypeTest {
  @Test
  void test_cookieTypeOneValue() {
    assertEquals(CookieType.APID, CookieType.valueOf(1));
  }

  @Test
  void test_cookieTypeTwoValue() {
    assertEquals(CookieType.BCOOKIE, CookieType.valueOf(2));
  }

  @Test
  void test_cookieTypeThreeValue() {
    assertEquals(CookieType.BOTH, CookieType.valueOf(3));
  }

  @Test
  void test_outOfBoundsCookieTypeIsNull() {
    assertNull(CookieType.valueOf(12));
  }
}
