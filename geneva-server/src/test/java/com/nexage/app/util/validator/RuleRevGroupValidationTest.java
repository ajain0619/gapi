package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RuleRevGroupValidationTest {

  private final RuleRevGroupValidation validation = new RuleRevGroupValidation();

  @Test
  void shouldBeValidWhenDataIsValid() {
    assertTrue(validation.isValid("1,2"));
  }

  @Test
  void shouldBeInvalidWhenDataIsInvalid() {
    assertFalse(validation.isValid("abc,2"));
  }

  @Test
  void shouldBeInvalidWhenDataContainsDuplicates() {
    assertFalse(validation.isValid("2,2"));
  }
}
