package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AdSizeTypeTest {

  @Test
  void shouldNotExistIfCodeIsOutOfRange() {
    AdSizeType type = AdSizeType.fromCode(4);
    assertNull(type);
  }

  @Test
  void shouldExistWithRightCode() {
    AdSizeType type = AdSizeType.fromCode(1);
    assertNotNull(type);
  }
}
