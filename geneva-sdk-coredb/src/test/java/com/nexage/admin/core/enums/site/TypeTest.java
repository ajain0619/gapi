package com.nexage.admin.core.enums.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TypeTest {
  @Test
  void shouldReturnPublisherSiteTypeWithGivenName() {
    assertEquals(Type.APPLICATION, Type.fromString("APPLICATION"));
  }

  @Test
  void shouldReturnNullIfNoPublisherSiteTypeWithGivenName() {
    assertNull(Type.fromString("NOT_A_TYPE"));
  }
}
