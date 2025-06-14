package com.nexage.admin.core.enums.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EthnicityTest {

  @Test
  void shouldMapFromEthnicityToCode() {
    String ethnicity = Ethnicity.defaultStringFromMap.get(Ethnicity.Hispanic);
    assertNotNull(ethnicity);
    assertEquals("2", ethnicity);
  }

  @Test
  void shouldMapFromCoreToEthnicity() {
    Ethnicity ethnicity = Ethnicity.defaultStringToMap.get("2");
    assertNotNull(ethnicity);
    assertEquals(Ethnicity.Hispanic, ethnicity);
  }

  @Test
  void shouldFailOnWrongInput() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Ethnicity.valueOf("10"));
    assertNotNull(exception);
  }
}
