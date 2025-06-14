package com.nexage.admin.core.enums.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MaritalStatusTest {

  @Test
  void shouldMapFromMaritalStatusToCode() {
    String maritalStatus = MaritalStatus.defaultStringFromMap.get(MaritalStatus.Married);
    assertNotNull(maritalStatus);
    assertEquals("M", maritalStatus);
  }

  @Test
  void shouldMapFromCoreToMaritalStatus() {
    MaritalStatus maritalStatus = MaritalStatus.defaultStringToMap.get("D");
    assertNotNull(maritalStatus);
    assertEquals(MaritalStatus.Divorced, maritalStatus);
  }

  @Test
  void shouldFailOnWrongInput() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Ethnicity.valueOf("Binary"));
    assertNotNull(exception);
  }
}
