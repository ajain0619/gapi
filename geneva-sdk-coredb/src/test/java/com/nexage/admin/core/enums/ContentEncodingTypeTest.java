package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ContentEncodingTypeTest {
  @Test
  void shouldReturnContentEncodingTypeWithGivenName() {
    assertEquals(ContentEncodingType.GZIP, ContentEncodingType.fromString("GZIP"));
  }

  @Test
  void shouldReturnNullIfNoContentEncodingTypeWithGivenName() {
    assertNull(ContentEncodingType.fromString("NOT_A_TYPE"));
  }
}
