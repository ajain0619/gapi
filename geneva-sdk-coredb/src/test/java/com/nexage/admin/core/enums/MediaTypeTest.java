package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MediaTypeTest {

  @Test
  void shouldThrowErrorWhenValueNotInEnum() {
    assertThrows(IllegalArgumentException.class, () -> MediaType.of("unknown"));
  }
}
