package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.AdFormatType;
import org.junit.jupiter.api.Test;

class AdFormatTypeTest {
  @Test
  void shouldReturnExpectedOrder() {
    assertEquals(0, AdFormatType.BANNER.ordinal());
    assertEquals(1, AdFormatType.VIDEO.ordinal());
  }
}
