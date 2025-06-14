package com.nexage.admin.core.enums.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GenderTest {

  @Test
  void shouldMapFromGenderToCode() {
    String gender = Gender.defaultStringFromMap.get(Gender.Female);
    assertNotNull(gender);
    assertEquals("F", gender);
  }

  @Test
  void shouldMapFromCoreToGender() {
    Gender gender = Gender.defaultStringToMap.get("F");
    assertNotNull(gender);
    assertEquals(Gender.Female, gender);
  }

  @Test
  void shouldFailOnWrongInput() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Ethnicity.valueOf("Binary"));
    assertNotNull(exception);
  }
}
